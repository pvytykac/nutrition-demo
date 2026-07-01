import { type ReactNode, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { User, UserManager, WebStorageStateStore } from 'oidc-client-ts'

import { AuthContext } from './AuthContext'

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [isInitialized, setIsInitialized] = useState(false)
  const userManagerRef = useRef<UserManager | null>(null)
  const processedRef = useRef(false)

  useEffect(() => {
    const authorityUrl = 'http://localhost:8000'

    const mgr = new UserManager({
      authority: authorityUrl + '/realms/nutrition',
      metadata: {
        issuer: authorityUrl + '/realms/nutrition',
        authorization_endpoint: authorityUrl + '/realms/nutrition/protocol/openid-connect/auth',
        token_endpoint: authorityUrl + '/realms/nutrition/protocol/openid-connect/token',
        userinfo_endpoint: authorityUrl + '/realms/nutrition/protocol/openid-connect/userinfo',
        end_session_endpoint: authorityUrl + '/realms/nutrition/protocol/openid-connect/logout',
        jwks_uri: authorityUrl + '/realms/nutrition/protocol/openid-connect/certs',
        check_session_iframe: authorityUrl + '/realms/nutrition/protocol/openid-connect/login-status-iframe.html',
      },
      client_id: 'ui',
      redirect_uri: 'http://localhost:5173/callback',
      post_logout_redirect_uri: 'http://localhost:5173',
      response_type: 'code',
      scope: 'openid profile email',
      automaticSilentRenew: true,
      userStore: new WebStorageStateStore({ store: window.sessionStorage }),
    })

    userManagerRef.current = mgr

    const onUserLoaded = (loadedUser: User) => setUser(loadedUser)
    const onSilentRenewError = () => console.error('Silent token renewal failed')
    const onAccessTokenExpiring = () => {
      mgr.signinSilent().catch(() => mgr.signoutRedirect())
    }

    mgr.events.addUserLoaded(onUserLoaded)
    mgr.events.addSilentRenewError(onSilentRenewError)
    mgr.events.addAccessTokenExpiring(onAccessTokenExpiring)

    const params = new URLSearchParams(window.location.search)
    const hasCallbackParams = params.has('code') && params.has('state')

    const initAuth = async () => {
      if (hasCallbackParams && !processedRef.current) {
        processedRef.current = true
        try {
          const callbackUser = await mgr.signinRedirectCallback()
          setUser(callbackUser)
          const returnUrl = sessionStorage.getItem('auth_return_url') || '/'
          sessionStorage.removeItem('auth_return_url')
          navigate(returnUrl, { replace: true })
        } catch {
          const storedUser = await mgr.getUser()
          setUser(storedUser ?? null)
        }
      } else {
        const storedUser = await mgr.getUser()
        setUser(storedUser ?? null)
      }
      setIsInitialized(true)
    }

    initAuth()

    return () => {
      mgr.events.removeUserLoaded(onUserLoaded)
      mgr.events.removeSilentRenewError(onSilentRenewError)
      mgr.events.removeAccessTokenExpiring(onAccessTokenExpiring)
    }
  }, [navigate])

  const login = useCallback(() => {
    sessionStorage.setItem('auth_return_url', window.location.pathname)
    userManagerRef.current?.signinRedirect()
  }, [])

  const logout = useCallback(() => {
    userManagerRef.current?.signoutRedirect()
  }, [])

  const getToken = useCallback(() => {
    return user?.access_token
  }, [user])

  const value = useMemo(
    () => ({
      user,
      isAuthenticated: !!user && !user.expired,
      isInitialized,
      login,
      logout,
      getToken,
    }),
    [user, isInitialized, login, logout, getToken],
  )

  return (
    <AuthContext.Provider value={value}>
      {children}
    </AuthContext.Provider>
  )
}
