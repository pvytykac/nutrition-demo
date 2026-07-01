## Implementation Tasks

### 1. Database Migration and Entities

- [ ] 1.1 Create Flyway migration `V2__create_nutrients.sql` with `nutrients` and `nutrient_votes` tables (including FK, unique constraints, indexes) and INSERT seed data (carbohydrates, protein, fat, phenylalanine)
- [ ] 1.2 Create `NutrientUnit` enum (GRAM, MILLIGRAM)
- [ ] 1.3 Create `Nutrient` entity (id, name, kcalPerGram, defaultUnit, status, source, authorId, createdAt)
- [ ] 1.4 Create `NutrientVote` entity (id, nutrient, voterId, createdAt) with unique constraint on (nutrient, voterId)
- [ ] 1.5 Create `NutrientRepository` (JpaRepository + JpaSpecificationExecutor)
- [ ] 1.6 Create `NutrientVoteRepository` (JpaRepository)

### 2. Request/Response DTOs

- [ ] 2.1 Create `NutrientRequestDTO` (name, kcalPerGram, defaultUnit) with validation
- [ ] 2.2 Create `NutrientResponseDTO` (id, name, kcalPerGram, defaultUnit, status, source, authorId, createdAt, _links)
- [ ] 2.3 Create `SuggestionRequestDTO` (name, kcalPerGram, defaultUnit) with validation
- [ ] 2.4 Create `SuggestionResponseDTO` (id, name, kcalPerGram, defaultUnit, status, source, authorId, voteCount, createdAt, _links)
- [ ] 2.5 Create HAL link builders for both controllers

### 3. Services

- [ ] 3.1 Create `NutrientService` with: create (admin), findAll (with name filter + sort), findById, update, delete
- [ ] 3.2 Add suggestion methods to `NutrientService`: suggest, findAllSuggestions (with vote count), findSuggestionById, vote (with auto-approval at 10), approve
- [ ] 3.3 Implement auto-approval logic in vote flow — flip status to ACTIVE when 10th vote is cast (use PESSIMISTIC_WRITE lock)

### 4. Controllers

- [ ] 4.1 Create `NutrientsController` with endpoints at `/v1/nutrients` (admin CRUD, any auth reads) with HAL links
- [ ] 4.2 Create `NutrientSuggestionsController` with endpoints at `/v1/nutrient-suggestions` (suggest, vote, approve, list) with HAL links
- [ ] 4.3 Remove the existing stub `NutrientsController` (the one with inline Nutrient record)

### 5. Tests

- [ ] 5.1 Write `NutrientServiceTest` — test create, find, update, delete, duplicate name, name filter, sort
- [ ] 5.2 Write suggestion service tests — test suggest, vote (single + duplicate), auto-approval at threshold, admin approve
- [ ] 5.3 Write `NutrientsControllerTest` — test CRUD endpoints with admin/user/unauthenticated auth, validation, HAL links
- [ ] 5.4 Write `NutrientSuggestionsControllerTest` — test suggestion submission, voting, listing, admin approve, HAL links, auth gating
- [ ] 5.5 Write `NutrientRepositoryTest` — test save, find, unique constraint

### 6. Final Verification

- [ ] 6.1 Run `./mvnw test` in backend and verify all tests pass
- [ ] 6.2 Run `openspec validate nutrients-module --type change --strict` before archive
