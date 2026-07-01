import { NavLink } from 'react-router-dom'
import { useAuth } from '../auth/useAuth'
import styles from './AppLayout.module.css'

const navItems = [
  { to: '/ingredients', label: 'Ingredients' },
  { to: '/nutrients', label: 'Nutrients' },
  { to: '/recipes', label: 'Recipes' },
]

interface SidebarProps {
  open: boolean
  onToggle: () => void
}

export function Sidebar({ open, onToggle }: SidebarProps) {
  const { logout, user } = useAuth()

  return (
    <aside className={`${styles.sidebar} ${open ? styles.sidebarOpen : ''}`}>
      <div className={styles.sidebarHeader}>
        <h2>Nutrition Demo</h2>
        <button className={styles.closeButton} onClick={onToggle} aria-label="Close navigation">
          &times;
        </button>
      </div>

      <nav className={styles.nav}>
        {navItems.map((item) => (
          <NavLink
            key={item.to}
            to={item.to}
            end
            onClick={onToggle}
            className={({ isActive }) =>
              `${styles.navItem} ${isActive ? styles.navItemActive : ''}`
            }
          >
            {item.label}
          </NavLink>
        ))}
      </nav>

      <div className={styles.sidebarFooter}>
        <span className={styles.username}>{user?.profile?.preferred_username ?? 'User'}</span>
        <button onClick={logout} className={styles.logoutButton}>
          Logout
        </button>
      </div>
    </aside>
  )
}
