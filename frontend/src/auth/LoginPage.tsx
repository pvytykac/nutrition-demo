import { useAuth } from './useAuth'

export function LoginPage() {
  const { login } = useAuth()

  return (
    <div style={{
      display: 'flex',
      flexDirection: 'column',
      alignItems: 'center',
      justifyContent: 'center',
      minHeight: '100vh',
      gap: '1rem',
    }}>
      <h1>Nutrition Demo</h1>
      <p style={{ color: 'var(--color-text-secondary)' }}>
        Sign in to manage your nutrition data
      </p>
      <button
        onClick={login}
        style={{
          padding: '0.75rem 2rem',
          backgroundColor: 'var(--color-primary)',
          color: '#fff',
          border: 'none',
          borderRadius: '4px',
          fontSize: '1rem',
          fontWeight: 600,
        }}
      >
        Sign in with Keycloak
      </button>
    </div>
  )
}
