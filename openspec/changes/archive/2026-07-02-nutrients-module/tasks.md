## Implementation Tasks

### 1. Database Migration and Entities

- [x] 1.1 Create Flyway migration `V2__create_nutrients.sql` with `nutrients` and `nutrient_votes` tables (including FK, unique constraints, indexes) and INSERT seed data (carbohydrates, protein, fat, phenylalanine)
- [x] 1.2 Create `NutrientUnit` enum (GRAM, MILLIGRAM)
- [x] 1.3 Create `Nutrient` entity (id, name, kcalPerGram, defaultUnit, status, source, authorId, createdAt)
- [x] 1.4 Create `NutrientVote` entity (id, nutrient, voterId, createdAt) with unique constraint on (nutrient, voterId)
- [x] 1.5 Create `NutrientRepository` (JpaRepository + JpaSpecificationExecutor)
- [x] 1.6 Create `NutrientVoteRepository` (JpaRepository)

### 2. Request/Response DTOs

- [x] 2.1 Create `NutrientRequestDTO` (name, kcalPerGram, defaultUnit) with validation
- [x] 2.2 Create `NutrientResponseDTO` (id, name, kcalPerGram, defaultUnit, status, source, authorId, createdAt, _links)
- [x] 2.3 Create `SuggestionRequestDTO` (name, kcalPerGram, defaultUnit) with validation
- [x] 2.4 Create `SuggestionResponseDTO` (id, name, kcalPerGram, defaultUnit, status, source, authorId, voteCount, createdAt, _links)
- [x] 2.5 Create HAL link builders for both controllers

### 3. Services

- [x] 3.1 Create `NutrientService` with: create (admin), findAll (with name filter + sort), findById, update, delete
- [x] 3.2 Add suggestion methods to `NutrientService`: suggest, findAllSuggestions (with vote count), findSuggestionById, vote (with auto-approval at 10), approve
- [x] 3.3 Implement auto-approval logic in vote flow — flip status to ACTIVE when 10th vote is cast (use PESSIMISTIC_WRITE lock)

### 4. Controllers

- [x] 4.1 Create `NutrientsController` with endpoints at `/v1/nutrients` (admin CRUD, any auth reads) with HAL links
- [x] 4.2 Create `NutrientSuggestionsController` with endpoints at `/v1/nutrient-suggestions` (suggest, vote, approve, list) with HAL links
- [x] 4.3 Remove the existing stub `NutrientsController` (the one with inline Nutrient record)

### 5. Tests

- [x] 5.1 Write `NutrientServiceTest` — test create, find, update, delete, duplicate name, name filter, sort
- [x] 5.2 Write suggestion service tests — test suggest, vote (single + duplicate), auto-approval at threshold, admin approve
- [x] 5.3 Write `NutrientsControllerTest` — test CRUD endpoints with admin/user/unauthenticated auth, validation, HAL links
- [x] 5.4 Write `NutrientSuggestionsControllerTest` — test suggestion submission, voting, listing, admin approve, HAL links, auth gating
- [x] 5.5 Write `NutrientRepositoryTest` — test save, find, unique constraint

### 6. Frontend — Nutrient Management Pages

- [x] 6.1 Add `@Relation(collectionRelation = "nutrients")` and `@Relation(collectionRelation = "suggestions")` to DTOs for clean HAL JSON keys
- [x] 6.2 Create `src/api/nutrients.ts` — typed API functions for nutrient CRUD, suggestions, voting, approval
- [x] 6.3 Create tabbed `NutrientsPage` — Active tab (name, kcalPerGram, defaultUnit columns) with admin edit/delete via link discovery, Suggestions tab (name, kcalPerGram, defaultUnit, votes, author columns) with user vote and admin approve via link discovery, role-based create/suggest button driven by collection-level HAL links
- [x] 6.4 Create `NutrientForm` — modal form for create/edit with name, kcalPerGram, defaultUnit fields
- [x] 6.5 (Merged into 6.3) Suggestions table integrated into tabbed NutrientsPage — standalone NutrientSuggestionsPage file removed
- [x] 6.6 Create `SuggestForm` — modal form for suggesting a new nutrient
- [x] 6.7 Update `App.tsx` routes — `/nutrients` → `NutrientsPage`, `/nutrients/suggestions` → `NutrientSuggestionsPage`
- [x] 6.8 Update `Sidebar.tsx` — add "Suggestions" nav item under Nutrients

### 7. Final Verification

- [x] 7.1 Run `./mvnw test` in backend and verify all tests pass
- [x] 7.2 Run `npm run lint` in frontend and verify no errors
- [x] 7.3 Run `openspec validate nutrients-module --type change --strict` before archive
