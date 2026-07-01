import { type ReactNode } from 'react'
import { useAuth } from './useAuth'
import { LoginPage } from './LoginPage'

interface AuthGuardProps {
  children: ReactNode
}

export function AuthGuard({ children }: AuthGuardProps) {
  const { isInitialized, isAuthenticated } = useAuth()

  if (!isInitialized) {
    return <div style={{ padding: '2rem', textAlign: 'center' }}>Loading...</div>
  }

  if (!isAuthenticated) {
    return <LoginPage />
  }

  return <>{children}</>
}
