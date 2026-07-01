## ADDED Requirements

### Requirement: User can suggest a new nutrient

Any authenticated user with the user role can submit a nutrient suggestion. The suggestion must include a name, optional kcal per gram, and a default unit. Created suggestions have status SUGGESTED and source SUGGESTION.

#### Scenario: User submits a nutrient suggestion
- **GIVEN** an authenticated user
- **WHEN** they POST a valid suggestion payload to `/v1/nutrient-suggestions`
- **THEN** the response is `201 Created`
- **AND** the body includes the suggestion with status `SUGGESTED` and source `SUGGESTION`
- **AND** the response includes HATEOAS `_links` with `self` and `vote`

#### Scenario: Admin cannot suggest
- **GIVEN** an authenticated admin user
- **WHEN** they POST a suggestion payload to `/v1/nutrient-suggestions`
- **THEN** the response is `403 Forbidden`

#### Scenario: Duplicate name returns conflict
- **GIVEN** a nutrient "Carbohydrates" already exists (ACTIVE or SUGGESTED)
- **WHEN** a user POSTs a suggestion with name "Carbohydrates"
- **THEN** the response is `409 Conflict`

### Requirement: Any authenticated user can list open suggestions

Suggestions with status SUGGESTED are returned as a paginated list with vote counts.

#### Scenario: User lists open suggestions
- **GIVEN** 3 SUGGESTED and 2 ACTIVE nutrients exist
- **WHEN** a user GETs `/v1/nutrient-suggestions`
- **THEN** the response is `200 OK` with a page containing only the 3 suggestions
- **AND** each suggestion includes a `voteCount` field
- **AND** the response includes HATEOAS `_links`

### Requirement: User can vote on a suggestion

Any authenticated user with the user role can vote once per suggestion. Votes are idempotent.

#### Scenario: User votes on a suggestion
- **GIVEN** a suggestion with id `abc-123` and an authenticated user
- **WHEN** they POST to `/v1/nutrient-suggestions/abc-123/votes`
- **THEN** the response is `200 OK`
- **AND** the vote count for the suggestion increments by 1

#### Scenario: User cannot vote twice
- **GIVEN** a suggestion `abc-123` and a user who already voted
- **WHEN** they POST to `/v1/nutrient-suggestions/abc-123/votes` again
- **THEN** the response is `200 OK`
- **AND** the vote count does not change

#### Scenario: User cannot vote on an approved suggestion
- **GIVEN** a suggestion that is no longer SUGGESTED (e.g., already approved)
- **WHEN** a user POSTs to `/v1/nutrient-suggestions/abc-123/votes`
- **THEN** the response is `400 Bad Request`

### Requirement: Suggestion is auto-approved at vote threshold

When a suggestion reaches 10 votes, it is automatically approved and its status changes to ACTIVE.

#### Scenario: 10th vote triggers auto-approval
- **GIVEN** a suggestion with 9 votes
- **WHEN** a user casts the 10th vote
- **THEN** the response is `200 OK`
- **AND** the suggestion's status changes to `ACTIVE`
- **AND** the suggestion disappears from the suggestions listing
- **AND** the nutrient is now visible via `GET /v1/nutrients/{id}`

#### Scenario: Admin can approve a suggestion instantly
- **GIVEN** a suggestion with id `abc-123` (status SUGGESTED)
- **WHEN** an admin POSTs to `/v1/nutrient-suggestions/abc-123/approve`
- **THEN** the response is `200 OK`
- **AND** the suggestion status changes to `ACTIVE`
- **AND** the nutrient is available via the nutrients API

### Requirement: Any authenticated user can get a suggestion by id

#### Scenario: User fetches existing suggestion
- **GIVEN** a SUGGESTED suggestion with id `abc-123`
- **WHEN** any authenticated user GETs `/v1/nutrient-suggestions/abc-123`
- **THEN** the response is `200 OK`
- **AND** the body includes the suggestion with fine-grained HATEOAS `_links` including `vote` and `approve` (approve only for admins)

#### Scenario: User fetches non-existing suggestion
- **GIVEN** no suggestion with id `abc-123`
- **WHEN** a user GETs `/v1/nutrient-suggestions/abc-123`
- **THEN** the response is `404 Not Found`
