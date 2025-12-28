/*----------------------------------------------------------------------------------------------------------------------
 * Evidence Engine: A system for managing evidence on arbitrary scientific topics.
 * Comprises an SQL database, GraphQL public API, Java app server, Java and web clients.
 * Copyright © 2024-25 Adrian Price. All rights reserved.
 *
 * This file is part of Evidence Engine.
 *
 * Evidence Engine is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * Evidence Engine is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with Evidence Engine.
 * If not, see <https://www.gnu.org/licenses/>. 
 *--------------------------------------------------------------------------------------------------------------------*/

import AuthPayload from '@/app/model/AuthPayload'
import User from '@/app/model/User'
import { CURRENT_USER, LOGIN } from '@/lib/graphql-queries'
import { hook, LoggerEx } from '@/lib/logger'
import { isEqual } from '@/lib/utils'
import { ApolloClient, ErrorLike } from '@apollo/client'
import { useMutation, useQuery } from '@apollo/client/react'
import { createContext, useContext, useEffect, useCallback, ReactNode, useRef, useState } from 'react'
import { useInterval, useLocalStorage } from 'usehooks-ts'
import { jwtDecode } from "jwt-decode"
import { toast } from 'sonner'
import { AuthorityKind } from '@/app/model/schema'

const logger = new LoggerEx(hook, "[useAuth] ")

type CurrentUserResult = {
  currentUser?: User
}

type LoginResult = {
  login: AuthPayload
}

interface AuthContextType {
  loading: boolean
  error?: ErrorLike
  jwtToken: string | null
  user: User | null
  hasAuthority: (authority: AuthorityKind) => boolean
  login: (username: string, password: string) => Promise<ApolloClient.MutateResult<LoginResult>>
  logout: () => void
}

const defaultAuth : AuthContextType = {
  loading: false,
  error: undefined,
  jwtToken: null,
  user: null,
  hasAuthority: (authority: string) => false,
  login: (u, p) => {throw new Error("No AuthProvider context")},
  logout: () => {throw new Error("No AuthProvider context")},
}

const echo = (s: string) =>  s

export const AuthContext = createContext<AuthContextType>(defaultAuth)

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({children} : AuthProviderProps) {
  logger.debug("AuthProvider")

  // NOTE: the default (de)serializers stupidly wrap the value in double quotes (duh!)
  const [jwtToken, setJwtToken] = useLocalStorage("jwt-token", '', { serializer: echo, deserializer: echo })
  const [jwtExpiryInterval, setJwtExpiryInterval] = useState<number | null>(null)
  const [user, setUser] = useState<User | null>(null)
  const currentUserResult = useQuery<CurrentUserResult>(CURRENT_USER)
  const [loginOp, loginOpResult] = useMutation<LoginResult>(LOGIN)

  const handleExpiry = useCallback(() => {
    logger.info("AuthProvider.handleExpiry after %s ms", jwtExpiryInterval)
    toast.warning("Your authentication token has expired. Please sign in again to continue editing.")
    setJwtToken('')
    setUser(null)
    setJwtExpiryInterval(null)
  }, [])
  useInterval(handleExpiry, jwtExpiryInterval)

  const parseToken = useCallback((token: string | null) => {
    logger.debug("AuthProvider.parseToken")
    let expiryInterval = null
    if (token) {
      const decoded = jwtDecode(token)
      logger.trace("AuthProvider.parseToken: token=%o", decoded)
      if (decoded.exp) {
        expiryInterval = decoded.exp * 1000 - Date.now()
        if (expiryInterval <= 0) {
          logger.debug("AuthProvider.parseToken: token has already expired")
          setJwtToken('')
          setUser(null)
          expiryInterval = null
        }
      }
    }
    setJwtExpiryInterval(expiryInterval)
  }, [])

  // If there is initially a JWT token in local storage:
  // - parse it
  // - set an expiration timer
  // - fetch and store the current user
  const init = useCallback(() => {
    logger.debug("AuthProvider.init: jwtToken=%o, user=%o", jwtToken, user)
    if (jwtToken) {
      parseToken(jwtToken)
      // At first sight one might think that calling refetch() would result in a total of two executions of the
      // CURRENT_USER query - one from the initial call to useQuery() and the second from the call to refetch().
      // However, in practice and for reasons unknown, only one currentUser call is observed in the network log.
      currentUserResult.refetch().then(cur => {
        logger.debug("AuthProvider.init.refetch: currentUser=%o", user)
        setUser(cur.data?.currentUser ?? null)
      })
    }
  }, [jwtToken, user, parseToken, currentUserResult])
  useEffect(init, [])

  const login = useCallback((username: string, password: string) => {
    logger.debug("AuthProvider.login")
    const loginPromise = loginOp({
      variables: {
        username,
        password
      }
    })
    loginPromise.then(lr => {
      logger.trace("AuthProvider.login complete: authPayload=%o", lr.data?.login)
      const token = lr.data?.login.token ?? ''
      parseToken(token)
      setJwtToken(token)
      setUser(lr.data?.login.user ?? null)
    })
    return loginPromise
  }, [loginOp, parseToken, setJwtToken])

  const logout = useCallback(() => {
    logger.debug("AuthProvider.logout")
    setJwtToken('')
    setJwtExpiryInterval(null)
    setUser(null)
    // Also clear basic auth cookies, otherwise the server could treat the client as still authenticated.
    document.cookie = "JSESSIONID=; Max-Age=0"
    document.cookie = "remember-me=; Max-Age=0"
  }, [setJwtToken])

  const loading = currentUserResult.loading || loginOpResult.loading
  const error = currentUserResult.error || loginOpResult.error

  const hasAuthority = useCallback((authority: AuthorityKind) => user?.authorities?.includes(authority) ?? false, [user])

  logger.trace("AuthProvider: jwtToken=%s, user=%o", jwtToken, user)

  // For reasons unknown, the previous useMemo approach wasn't working and (according to WhyDidYouRender) returned different objects with equal values.
  let auth = { loading, error, jwtToken, user, hasAuthority, login, logout } as AuthContextType
  const prevAuth = useRef(defaultAuth)
  if (isEqual(auth, prevAuth.current)) {
    logger.trace("AuthProvider: reusing previous auth %o", prevAuth.current)
    auth = prevAuth.current
  } else {
    logger.trace("AuthProvider: auth has changed from %o to %o", prevAuth.current, auth)
    prevAuth.current = auth
  }

  return (
    <AuthContext.Provider
      value={auth}
    >
      {children}
    </AuthContext.Provider>
  )
}

export default function useAuth() : AuthContextType {
  logger.debug("call")

  const ctx = useContext(AuthContext)
  if (!ctx)
    throw new Error('useAuth must be inside AuthProvider')
  return ctx
}
