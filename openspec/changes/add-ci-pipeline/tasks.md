## Implementation Tasks

- [x] 1.0 Create `.github/workflows/ci.yml` with workflow triggers (push to main, PR targeting main)
- [x] 1.1 Add path filtering step using `dorny/paths-filter` to detect backend/ and frontend/ changes
- [x] 1.2 Add backend job — setup JDK 25, run `mvn clean test -f backend/pom.xml`, cache Maven dependencies
- [x] 1.3 Add frontend build+test job — setup Node 22, `npm ci`, `npm run build`, `npm run test`, cache npm dependencies
- [x] 1.4 Add frontend lint job (PR only) — `npm run lint`, conditional on frontend changes and PR event
- [x] 1.5 Handle early exit when no modules have changed (skip all jobs with a success status)
- [x] 2.0 Remove the old `maven.yml` workflow file (superseded by ci.yml)
- [x] 3.0 Run `openspec validate add-ci-pipeline --type change --strict` before archive
