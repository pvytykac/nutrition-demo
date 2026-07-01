## Context

The project has a Spring Boot 4 backend with three REST resources (`/v1/ingredients`, `/v1/nutrients`, `/v1/recipes`) and a planned React SPA frontend module (per AGENTS.md). Authentication is handled by a local Keycloak instance on `localhost:8000`. No frontend code exists yet. This design covers the initial skeleton: auth, navigation shell, and paged resource tables.

Backend pagination contract: `GET /v1/{resource}` returns `{ content: [...], page: { size, totalElements, totalPages, number } }` with default page size 20.

## Goals / Non-Goals

**Goals:**
- Scaffold the `frontend/` module with Vite + React + TypeScript
- Implement OIDC login/logout flow against the existing Keycloak instance
- Provide an app shell with a left sidebar navigation linking to 3 resource pages
- Render each resource as a paged table with loading, error, and empty states
- Proxy API and auth requests through Vite dev server to avoid CORS issues in development

**Non-Goals:**
- No CRUD operations beyond the GET listing (create, update, delete come later)
- No advanced filtering or search
- No real-time updates or WebSocket
- No comprehensive E2E tests (unit tests only for this skeleton)
- No production build optimizations beyond Vite defaults

## Decisions

### 1. Build Tool: Vite + React + TypeScript

Standard choice aligned with the planned tech stack. Vite provides fast HMR, TypeScript support out of the box, and a proxy configuration that avoids CORS during development.

**Alternatives considered:**
- CRA: Deprecated and slower than Vite
- Next.js: Overkill for an SPA that will be served separately from the backend

### 2. OIDC Library: oidc-client-ts

A lightweight, framework-agnostic OIDC library that handles the authorization code flow with PKCE. Provides token storage, silent token renewal, and automatic token injection.

**Alternatives considered:**
- `@react-keycloak/web`: Tighter React integration but more opinionated; less control over the auth flow
- `@auth0/auth0-react`: Vendor-specific; not designed for self-hosted Keycloak
- Manual OIDC flow: Too much work for auth-critical feature

### 3. Routing: React Router DOM v7

Declarative routing with loader/action support. Each resource page gets its own route (`/ingredients`, `/nutrients`, `/recipes`). Auth guard wraps all protected routes.

**Alternatives considered:**
- TanStack Router: More powerful but adds complexity for only 3 routes
- Manual state-based routing: Not scalable

### 4. HTTP Client: Native `fetch` wrapped in a custom hook

Avoids an extra dependency. A `useResourceList` hook encapsulates fetching, pagination state, loading/error handling, and the abort controller for cleanup.

**Alternatives considered:**
- axios: Adds ~15KB for sugar that provides little value for simple GET requests
- react-query (TanStack Query): Excellent caching, but over-engineered for this skeleton phase; can be added later

### 5. Styling: CSS Modules

Component-scoped CSS without adding a dependency. Keeps styles co-located with components. Easy to migrate to Tailwind or CSS-in-JS later.

**Alternatives considered:**
- Tailwind CSS: Adds a build step and utility class verbosity; better suited after the skeleton phase
- styled-components: Runtime CSS-in-JS overhead with no SSR benefit for an SPA
- Plain CSS: No scoping, risk of name collisions

### 6. Project Structure: Feature-Based

```
frontend/
├── index.html
├── vite.config.ts
├── tsconfig.json
├── package.json
└── src/
    ├── main.tsx               # App entry, OIDC init
    ├── App.tsx                # Router + AuthGuard + Layout
    ├── auth/
    │   ├── AuthProvider.tsx    # React context wrapping oidc-client-ts
    │   ├── useAuth.ts          # Auth context hook
    │   ├── LoginPage.tsx       # Login button page
    │   └── AuthGuard.tsx       # Route protection wrapper
    ├── layout/
    │   ├── AppLayout.tsx       # Shell: sidebar + content area
    │   ├── Sidebar.tsx         # Left sidebar nav
    │   └── AppLayout.module.css
    ├── resources/
    │   ├── ResourceListPage.tsx # Generic paged table page
    │   ├── ResourceTable.tsx    # Table component
    │   ├── useResourceList.ts   # Fetch + pagination hook
    │   ├── ResourceListPage.module.css
    │   └── ResourceTable.module.css
    └── api/
        └── client.ts           # fetch wrapper, base URL, token injection
```

### 7. Vite Proxy Configuration

A single proxy entry forwards everything under `/v1/` and `/realms/` to `http://localhost:8080`, so the frontend dev server handles auth redirects and API calls without CORS issues. The production build would be served behind the same origin via the backend or a reverse proxy.

## Risks / Trade-offs

- [Auth complexity] OIDC with PKCE has multiple redirect hops — a misconfigured Keycloak client can cause hard-to-debug loops. Mitigation: document the exact Keycloak client setup needed, test end-to-end early.
- [Proxy dependency] Vite proxy works only in dev. Production deployment needs the SPA served from the same origin as the API (or a proper reverse proxy). Mitigation: document that the production deployment strategy is a separate concern.
- [Generic table vs. specialisation] A single `ResourceListPage` component for all 3 resources works initially but will diverge once each resource needs resource-specific columns or actions. Mitigation: design the generic table with an extensible column config and a render prop pattern.
- [No tests in skeleton] Skipping component tests for now increases risk of regressions later. Mitigation: tasks include a basic smoke test setup.

## Open Questions

None at this point. All three resources follow the same pagination contract, so a single component approach is safe for the skeleton.
