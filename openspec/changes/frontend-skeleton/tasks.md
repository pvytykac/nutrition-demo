## Implementation Tasks

### 0. Infrastructure

- [x] 0.1 Create root-level `compose.yaml` that includes `backend/compose.yaml` for one-command startup of PostgreSQL, Keycloak, and Kafka
- [x] 0.2 Verify `docker compose up` from project root starts all services successfully

### 1. Scaffold frontend module

- [x] 1.1 Create `frontend/` directory and initialize npm project with `vite@latest create` using React + TypeScript template
- [x] 1.2 Install dependencies: `react-router-dom`, `oidc-client-ts`, and dev deps: `@types/react`, `@types/react-dom`, `vite`
- [x] 1.3 Configure `tsconfig.json` with path aliases and strict mode
- [x] 1.4 Create `vite.config.ts` with proxy rules for `/v1/*` and `/realms/*` to `http://localhost:8080` and `http://localhost:8000`
- [x] 1.6 Create `src/main.tsx` as the application entry point

### 2. Auth layer (login-screen spec)

- [x] 2.1 Create `src/auth/AuthProvider.tsx` — React context that initializes `UserManager` from oidc-client-ts, provides user/tokens to children, and handles silent renewal
- [x] 2.2 Create `src/auth/useAuth.ts` — hook to consume auth context (user, login, logout, isAuthenticated)
- [x] 2.3 Create `src/auth/LoginPage.tsx` — page with a "Sign in with Keycloak" button that calls `userManager.signinRedirect()`
- [x] 2.4 Create `src/auth/AuthGuard.tsx` — route wrapper that redirects unauthenticated users to `/login` and handles the OIDC signin callback on mount

### 3. Application shell and routing (nav-layout spec)

- [x] 3.1 Create `src/App.tsx` — root component with React Router `<BrowserRouter>`, `<Routes>`, and `<AuthGuard>` wrapping protected routes
- [x] 3.2 Create `src/layout/AppLayout.tsx` — shell layout with sidebar and `<Outlet />` for content
- [x] 3.3 Create `src/layout/Sidebar.tsx` — left sidebar with three `<NavLink>` items: Ingredients (`/ingredients`), Nutrients (`/nutrients`), Recipes (`/recipes`), plus a logout button
- [x] 3.4 Create `src/layout/AppLayout.module.css` — sidebar and layout styles with responsive collapse at 768px

### 4. Resource listing page (resource-table spec)

- [x] 4.1 Create `src/api/client.ts` — generic `apiFetch` wrapper that injects the Bearer token from auth and handles the base URL
- [x] 4.2 Create `src/resources/useResourceList.ts` — custom hook that accepts a resource path, fetches paginated data, and returns `{ items, page, loading, error, goToPage, retry }`
- [x] 4.3 Create `src/resources/ResourceTable.tsx` — generic table component with column config prop, pagination controls (Previous/Next, page info), loading spinner, empty state ("No items found"), and error state with retry button
- [x] 4.4 Create `src/resources/ResourceTable.module.css` — table and pagination styles
- [x] 4.5 Create `src/resources/ResourceListPage.tsx` — page component that uses `useResourceList` and `ResourceTable` with column config for ID and Name
- [x] 4.6 Register three routes in `App.tsx`: `/ingredients`, `/nutrients`, `/recipes` — all rendering `ResourceListPage` with their respective resource paths

### 5. Verify and document

- [x] 5.1 Run `npm run build` from `frontend/` and fix any TypeScript/build errors
- [x] 5.2 Update `AGENTS.md` with frontend build commands (npm install, npm run dev, npm run build)
