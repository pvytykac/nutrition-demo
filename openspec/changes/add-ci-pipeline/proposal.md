## Why

The project has two independent modules (backend and frontend) but no automated CI pipeline. Every change must be built and tested manually, risking regressions and making it impossible to enforce quality gates on pull requests.

## What Changes

- Add a GitHub Actions workflow that runs on push to `main` and pull requests targeting `main`
- On push to `main`: run build + test for changed modules
- On pull requests: run build + test + lint for changed modules
- Use path-based change detection so only modules with changes are built
- Introduce a `mvnw` wrapper (or use system Maven) for backend builds
- Frontend uses `npm` scripts: `npm ci`, `npm run build`, `npm run test`, `npm run lint`

## Capabilities

### New Capabilities

- `ci-pipeline` — GitHub Actions CI pipeline for backend and frontend

### Modified Capabilities

None. This is a new infrastructure capability, not a behavioural spec change.

## Impact

- New `.github/workflows/ci.yml` file added to the repository root
- Backend and frontend build, test, and lint commands must remain stable
- No changes to application code, API contracts, or runtime behaviour
