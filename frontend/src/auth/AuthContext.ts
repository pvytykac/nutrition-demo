import { createContext } from 'react'
import { User } from 'oidc-client-ts'

export interface AuthContextValue {
  user: User | null
  isAuthenticated: boolean
  isInitialized: boolean
  login: () => void
  logout: () => void
  getToken: () => string | undefined
}

export const AuthContext = createContext<AuthContextValue | null>(null)
