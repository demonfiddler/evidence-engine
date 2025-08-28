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
import { createContext, useContext, useState, useEffect, useCallback, ReactNode, useMemo } from 'react'

const GRAPHQL_ENDPOINT_URL = new URL(process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT_URL ?? '')
const baseUrl = getUrl('/').toString()
const loginUrl = getUrl("/login")
const logoutUrl = getUrl("/logout");
const graphQlUrl = getUrl("/graphql");

function getUrl(path: string, query?: string, fragment?: string) {
  const url = new URL(path, GRAPHQL_ENDPOINT_URL)
  if (query)
    url.search = query
  if (fragment)
    url.hash = fragment
  return url
}

interface AuthContextType {
  loading: boolean;
  user: User | null;
  hasAuthority: (authority: Authority) => boolean;
  login: (username: string, password: string, rememberMe: boolean) => Promise<void>;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType>({
  loading: false,
  user: null,
  hasAuthority: (authority: string) => false,
  login: (u, p) => {throw new Error("No AuthProvider context")},
  logout: () => {throw new Error("No AuthProvider context")}
});

function checkResponse(response: Response, action: string) {
  // As Spring Security is presently configured:
  // - POST to /login redirects to the base URL, which doesn't exist.
  // - POST to /logout redirects to the /login page, which does exist.
  if (response.status != 0 && !response.ok && !response.redirected || !(response.status == 0 && response.type == "opaqueredirect")) {
    console.log(`status = ${response.status}, statusText = ${response.statusText}, ok = ${response.ok}, redirected = ${response.redirected}, type = ${response.type}`)
    throw new Error(`${action} failed: status = ${response.status}: ${response.statusText}`);
  }
}

async function fetchCsrfToken() {
  const res = await fetch(loginUrl, { credentials: 'omit' });
  const html = await res.text();
  const doc = new DOMParser().parseFromString(html, 'text/html');
  const _csrf = doc.evaluate('//input[@name="_csrf"]/@value', doc, null, XPathResult.STRING_TYPE, null).stringValue
  return _csrf;
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
  console.debug("Logged out");
}

async function fetchUser() {
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
  });
  const { data, error } = await response.json()
  if (error)
    throw new Error(error)
  if (!data?.currentUser)
    // FIXME: don't throw this error for anonymous users.
    throw new Error("Invalid credentials")
  return data?.currentUser
}

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({children} : AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const initialize = useCallback(async () => {
    try {
      const me = await fetchUser()
      setUser(me)
    } catch (e) {
      console.error(e)
      setUser(null)
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => { initialize(); }, [initialize]);

  const hasAuthority = useCallback((authority: Authority) => user?.authorities?.includes(authority) ?? false, [user])

  const doLogin = useCallback(async (username: string, password: string, rememberMe: boolean) => {
    setLoading(true)
    try {
      await login(username, password, rememberMe)
      const me = await fetchUser()
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

  const auth = useMemo(() => {
    return { loading, user, hasAuthority, login: doLogin, logout: doLogout }
  }, [loading, user, hasAuthority, doLogin, doLogout])

  return (
    <AuthContext.Provider
      value={auth}
    >
      {children}
    </AuthContext.Provider>
  )
}

export default function useAuth() : AuthContextType {
  const ctx = useContext(AuthContext)
  if (!ctx)
    throw new Error('useAuth must be inside AuthProvider')
  return ctx
}
