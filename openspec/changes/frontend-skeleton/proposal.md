## Why

The project currently has only a backend REST API with no user interface. To make the application usable and demonstrate the API capabilities, we need a frontend SPA. This change creates the frontend module skeleton — login, navigation, and resource listing pages.

## What Changes

- Scaffold a new `frontend/` module with a TypeScript + React SPA
- Add a login screen that authenticates via Keycloak (OIDC authorization code flow)
- Add an application shell with a left-hand navigation sidebar containing 3 items: Ingredients, Nutrients, Recipes
- Add resource listing pages for each of the 3 resources, each displaying a paged table fetched from `GET /v1/{resource}`
- Configure the Vite dev server to proxy API and auth requests to the backend
- Add frontend build and dev commands to the project tooling

## Capabilities

### New Capabilities

- `login-screen` — OIDC login flow with redirect, token storage, and logout
- `nav-layout` — App shell with left sidebar nav, route-based page switching, and auth guard
- `resource-table` — Paged data table that fetches from a paginated REST endpoint, with loading/error/empty states

### Modified Capabilities

(None — no existing frontend capabilities to modify)

## Impact

- New `frontend/` directory with npm project, source code, and configuration
- Backend CORS configuration may need updates to allow requests from the frontend dev server origin
- No changes to existing backend code or APIs
- No breaking changes
