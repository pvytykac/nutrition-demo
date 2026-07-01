## Why

The nutrients module is currently a placeholder — a stub controller with hardcoded data. Nutrients are the foundation of the nutrition tracking system; every ingredient needs configured nutrients before nutritional data can be recorded. This change replaces the stub with a real module backed by a database, with full CRUD for admins and a community-driven suggestion-and-vote workflow for users.

## What Changes

- Implement `Nutrient` entity, repository, service, controller, DTOs, and Flyway migration in the backend `nutrient` module
- Single `nutrients` table with a `status` column (ACTIVE / SUGGESTED) unifying both admin-created and user-suggested nutrients
- `source` column (SEED / ADMIN / SUGGESTION) and `author_id` column for provenance tracking
- `nutrient_votes` table for community voting on suggestions
- Two REST controllers: `NutrientsController` (`/v1/nutrients`, ACTIVE only) and `NutrientSuggestionsController` (`/v1/nutrient-suggestions`, SUGGESTED only)
- Admin-only endpoints for direct nutrient CRUD and instant suggestion approval
- User-facing endpoints for suggesting nutrients and voting on suggestions
- Auto-approval of suggestions at 10 votes
- Input validation, pagination, name filtering, sorting, error handling, and authorization throughout
- HATEOAS links in HAL format (coarse on collections, fine on individual resources)
- Default seed nutrients inserted via Flyway: carbohydrates, protein, fat, phenylalanine
- Nutrient unit field (GRAM / MILLIGRAM) to support both macro and trace nutrients

## Capabilities

### New Capabilities

- `nutrient-crud` — Admin CRUD for nutrients: create, read, update, delete active nutrient definitions (name, kcal per gram, default unit)
- `nutrient-requests` — User suggestion and voting workflow: suggest a new nutrient, vote on open suggestions, auto-approve at vote threshold

### Modified Capabilities

None — the nutrient module has no existing behaviour specs.

## Impact

- **Database**: New `nutrients` table (with status/source/author_id) and `nutrient_votes` table via Flyway migration
- **Backend**: The `nutrient` module evolves from a stub to a complete Modulith module with entity, repository, service, controller, DTOs
- **Frontend**: Rich nutrient management UI with admin CRUD forms, community suggestion listing with voting and approval actions
- **API**: New REST endpoints under `/v1/nutrients` (ACTIVE, admin CRUD) and `/v1/nutrient-suggestions` (SUGGESTED, user + admin)
- **Security**: `@HasAdminRole` on mutation endpoints, `@HasUserRole` on suggestion/vote, `@HasUserOrAdminRole` on read endpoints
