import { type ReactNode, createContext, useCallback, useEffect, useMemo, useRef, useState } from 'react'
import { User, UserManager, WebStorageStateStore } from 'oidc-client-ts'

interface AuthContextValue {
  user: User | null
  isAuthenticated: boolean
  isInitialized: boolean
  login: () => void
  logout: () => void
  getToken: () => string | undefined
}

export const AuthContext = createContext<AuthContextValue | null>(null)

interface AuthProviderProps {
  children: ReactNode
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [user, setUser] = useState<User | null>(null)
  const [isInitialized, setIsInitialized] = useState(false)
  const userManagerRef = useRef<UserManager | null>(null)

  useEffect(() => {
    const mgr = new UserManager({
      authority: 'http://localhost:5173/realms/nutrition',
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

    mgr
      .signinRedirectCallback()
      .then((callbackUser) => {
        setUser(callbackUser)
        setIsInitialized(true)
        const returnUrl = sessionStorage.getItem('auth_return_url') || '/'
        sessionStorage.removeItem('auth_return_url')
        window.history.replaceState({}, document.title, returnUrl)
      })
      .catch(() => {
        mgr.getUser().then((storedUser) => {
          setUser(storedUser ?? null)
          setIsInitialized(true)
        })
      })

    return () => {
      mgr.events.removeUserLoaded(onUserLoaded)
      mgr.events.removeSilentRenewError(onSilentRenewError)
      mgr.events.removeAccessTokenExpiring(onAccessTokenExpiring)
    }
  }, [])

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
