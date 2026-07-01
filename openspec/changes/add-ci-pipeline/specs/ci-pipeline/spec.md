## ADDED Requirements

### Requirement: CI pipeline SHALL build and test changed modules

When code is pushed to the default branch or a pull request is opened, the CI pipeline SHALL run the build and test suite for each module that has changed files.

#### Scenario: Backend changes trigger backend build and test
- **GIVEN** a commit modifies files under `backend/`
- **WHEN** the commit is pushed to `main` or a PR is opened targeting `main`
- **THEN** the CI pipeline runs `mvn clean test` in the `backend/` directory
- **AND** the pipeline passes only if all tests succeed

#### Scenario: Frontend changes trigger frontend build and test
- **GIVEN** a commit modifies files under `frontend/`
- **WHEN** the commit is pushed to `main` or a PR is opened targeting `main`
- **THEN** the CI pipeline runs `npm ci` and `npm run test` in the `frontend/` directory
- **AND** the pipeline passes only if all tests succeed

#### Scenario: Changes in both modules trigger both pipelines
- **GIVEN** a commit modifies files under both `backend/` and `frontend/`
- **WHEN** the commit is pushed to `main` or a PR is opened targeting `main`
- **THEN** the CI pipeline runs the backend and frontend build+test jobs in parallel
- **AND** both jobs must pass for the overall pipeline to succeed

#### Scenario: Non-module changes skip all builds
- **GIVEN** a commit modifies only files outside `backend/` and `frontend/` (e.g., README, docs)
- **WHEN** the commit is pushed to `main` or a PR is opened targeting `main`
- **THEN** the CI pipeline exits with a "no changes to build" status
- **AND** it is reported as successful (not blocking the PR or push)

### Requirement: PR pipeline SHALL include linting

Pull requests SHALL run the linting tool in addition to build and test for changed modules.

#### Scenario: Backend PR includes linting
- **GIVEN** a PR modifies files under `backend/`
- **WHEN** the PR CI pipeline runs
- **THEN** it runs `mvn clean test` (build+test includes error-prone/NullAway checks already configured in pom.xml)

#### Scenario: Frontend PR runs linter
- **GIVEN** a PR modifies files under `frontend/`
- **WHEN** the PR CI pipeline runs
- **THEN** it runs `npm run lint` in addition to `npm ci`, `npm run build`, and `npm run test`
- **AND** the pipeline fails if lint produces any errors
