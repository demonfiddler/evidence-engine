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

import Authority from '@/app/model/Authority'
import User from '@/app/model/User'
import { hook, LoggerEx } from '@/lib/logger'
import { isEqual } from '@/lib/utils'
import { createContext, useContext, useState, useEffect, useCallback, ReactNode, useRef } from 'react'

const logger = new LoggerEx(hook, "[useAuth] ")

const GRAPHQL_ENDPOINT_URL = new URL(process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL ?? '')
const baseUrl = getUrl('/').toString()
const loginUrl = getUrl("/login")
const logoutUrl = getUrl("/logout")
const graphQlUrl = getUrl("/graphql")

function getUrl(path: string, query?: string, fragment?: string) {
  const url = new URL(path, GRAPHQL_ENDPOINT_URL)
  if (query)
    url.search = query
  if (fragment)
    url.hash = fragment
  return url
}

interface AuthContextType {
  loading: boolean
  user: User | null
  hasAuthority: (authority: Authority) => boolean
  login: (username: string, password: string, rememberMe: boolean) => Promise<void>
  logout: () => void
}

const defaultAuth : AuthContextType = {
  loading: false,
  user: null,
  hasAuthority: (authority: string) => false,
  login: (u, p) => {throw new Error("No AuthProvider context")},
  logout: () => {throw new Error("No AuthProvider context")},
}

export const AuthContext = createContext<AuthContextType>(defaultAuth)

function checkResponse(response: Response, action: string) {
  // As Spring Security is presently configured:
  // - POST to /login redirects to the base URL, which doesn't exist.
  // - POST to /logout redirects to the /login page, which does exist.
  if (response.status != 0 && !response.ok && !response.redirected || !(response.status == 0 && response.type == "opaqueredirect")) {
    logger.trace("status = %d, statusText = %s, ok = %s, redirected = %s, type = %s", response.status, response.statusText, response.ok, response.redirected, response.type)
    throw new Error(`${action} failed: status = ${response.status}: ${response.statusText}`)
  }
}

async function fetchCsrfToken() {
  const res = await fetch(loginUrl, { credentials: 'omit' })
  const html = await res.text()
  const doc = new DOMParser().parseFromString(html, 'text/html')
  const _csrf = doc.evaluate('//input[@name="_csrf"]/@value', doc, null, XPathResult.STRING_TYPE, null).stringValue
  return _csrf
}

async function login(username: string, password: string, rememberMe: boolean) {
  const _csrf = await fetchCsrfToken();
  const response = await fetch(loginUrl, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    redirect: "manual",
    body: new URLSearchParams({ username, password, _csrf, "remember-me": rememberMe ? "on" : "" }),
  });
  checkResponse(response, "Login")
  logger.info("Signed in successfully")
}

async function logout() {
  const response = await fetch(logoutUrl, {
    method: "POST",
    mode: "cors",
    headers: new Headers({
      "Accept": "text/html,application/xhtml+xml,application/xml",
      "Origin": baseUrl
    }),
    redirect: "manual",
    cache: "no-store",
    credentials: "include",
  })
  document.cookie = 'JSESSIONID=; Max-Age=0'
  document.cookie = 'remember-me=; Max-Age=0'
  checkResponse(response, "Logout")
  logger.info("Signed out successfully")
}

async function fetchUser(requireUser: boolean) {
  const response = await fetch(graphQlUrl, {
    method: 'POST',
    credentials: 'include',
    headers: { 'Content-Type': 'application/json' },
    redirect: "manual",
    body: JSON.stringify({
      query: `
        query {
          currentUser {
            id
            username
            firstName
            lastName
            email
            country(format:ALPHA_2)
            notes
            authorities(aggregation: ALL, format: SHORT)
          }
        }
      `
    }),
  })
  const { data, error } = await response.json()
  if (error)
    throw new Error(error)
  if (requireUser && !data?.currentUser)
    throw new Error("Invalid credentials")
  return data?.currentUser
}

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({children} : AuthProviderProps) {
  logger.debug("AuthProvider render")

  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)

  const initialize = useCallback(async () => {
    try {
      const me = await fetchUser(false)
      setUser(me)
      logger.trace("initialize: user initialised to %o", me)
    } catch (e) {
      logger.error("Caught exception: %o", String(e))
      setUser(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { initialize() }, [initialize])

  // const prevUser = useRef(user)
  // if (user !== prevUser.current) {
  //   if (isEqual(user, prevUser.current))
  //     logger.trace("render: user has changed but is equal in value: %o", prevUser.current)
  //   else
  //     logger.trace("render: user has changed from %o to %o", prevUser.current, user)
  //   prevUser.current = user
  // }
  const hasAuthority = useCallback((authority: Authority) => user?.authorities?.includes(authority) ?? false, [user])
  // const prevHasAuthority = useRef(hasAuthority)
  // if (hasAuthority !== prevHasAuthority.current) {
  //   logger.trace("render: hasAuthority has changed from %s to %s", typeof prevHasAuthority.current, typeof hasAuthority)
  // }
  logger.trace("render: hasAuthority=%s", typeof hasAuthority)

  const doLogin = useCallback(async (username: string, password: string, rememberMe: boolean) => {
    setLoading(true)
    try {
      await login(username, password, rememberMe)
      const me = await fetchUser(true)
      setUser(me)
    } finally {
      setLoading(false)
    }
  }, [])

  const doLogout = useCallback(async () => {
    setLoading(true)
    try {
      await logout()
    } finally {
      setLoading(false)
      setUser(null)
    }
  }, [])

  // For reasons unknown, the previous useMemo approach wasn't working and (according to WhyDidYouRender) returned different objects with equal values.
  const prevAuth = useRef(defaultAuth)
  let auth = { loading, user, hasAuthority, login: doLogin, logout: doLogout } as AuthContextType
  if (isEqual(auth, prevAuth.current)) {
    logger.trace("render: reusing previous auth %o", prevAuth.current)
    auth = prevAuth.current
  } else {
    logger.trace("render: auth has changed from %o to %o", prevAuth.current, auth)
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
