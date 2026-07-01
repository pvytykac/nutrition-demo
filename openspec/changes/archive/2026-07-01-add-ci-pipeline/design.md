## Context

The nutrition-demo project has two modules (backend/ and frontend/) with separate build toolchains. Currently there is no CI pipeline — all building, testing, and linting is done manually. GitHub Actions is the team's CI platform and is available at no additional cost for public/private repos.

## Goals / Non-Goals

**Goals:**
- Automatically build and test the backend and frontend on every push to `main`
- Automatically build, test, and lint on every pull request targeting `main`
- Run each module's pipeline only when relevant files change (path filtering)
- Notify contributors of pipeline results via GitHub commit/PR status checks

**Non-Goals:**
- Deploy or release automation (future concern)
- Cross-module integration tests (docker-compose based — future concern)
- Code quality reporting (SonarQube, Codecov, etc.)

## Decisions

### 1. Single workflow file with reusable workflows

Use a single `.github/workflows/ci.yml` with one job per module. GitHub Actions matrix strategy is avoided in favour of explicit jobs for clarity.

**Alternatives considered:**
- Matrix strategy: More compact YAML but harder to read and maintain. Since there are only two modules, explicit jobs are clearer.
- Separate workflow files per module: Unnecessary duplication for only two modules.

### 2. Path-based change detection with paths-filter

Use `dorny/paths-filter` GitHub Action to determine which modules have changed. This avoids running backend tests when only frontend changes and vice versa.

**Alternatives considered:**
- `paths` / `paths-ignore` on workflow/job level: Simpler but runs the entire job or not — doesn't let us skip individual jobs within a shared workflow.

### 3. Backend uses Maven with JDK 25

The backend requires Java 25 (per `pom.xml`). Use `actions/setup-java` with Temurin JDK 25 distribution. Run `mvn clean test` for build+test.

### 4. Frontend uses Node.js with npm

The frontend requires Node.js (modern LTS, e.g. 22.x). Run `npm ci`, `npm run build`, `npm run test` on all events, plus `npm run lint` on PRs.

### 5. Separate PR and main triggers

- `push: branches: [main]` — run build + test (skip lint to keep pushes fast)
- `pull_request: branches: [main]` — run build + test + lint

## Risks / Trade-offs

- **Risk**: Pipeline runs even when only non-module files change (docs, README). *Mitigation*: `paths-filter` will skip all jobs; the workflow exits with "no changes to build".
- **Risk**: CI fails because of missing `mvnw` wrapper. *Mitigation*: Use `actions/setup-java` with Maven wrapper via `mvn` (system-installed Maven via setup-java cache).
- **Risk**: Frontend lint runner (`oxlint`) may produce different results in CI vs local. *Mitigation*: Use exact version pinned in `package.json` (`^1.71.0`).
