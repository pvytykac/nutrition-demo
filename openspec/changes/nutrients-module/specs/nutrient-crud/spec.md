## ADDED Requirements

### Requirement: Admin can create a new active nutrient

Only authenticated users with the admin role can create nutrients. A nutrient has a name, kcal per gram (optional), and a default unit (GRAM or MILLIGRAM). Created nutrients have status ACTIVE and source ADMIN.

#### Scenario: Admin creates a nutrient successfully
- **GIVEN** an authenticated admin user
- **WHEN** they POST a valid nutrient payload to `/v1/nutrients`
- **THEN** the response is `201 Created`
- **AND** the body includes the nutrient with status `ACTIVE` and source `ADMIN`
- **AND** the response includes HATEOAS `_links` with `self`, `edit`, and `delete`

#### Scenario: Non-admin cannot create a nutrient
- **GIVEN** an authenticated non-admin user
- **WHEN** they POST a nutrient payload to `/v1/nutrients`
- **THEN** the response is `403 Forbidden`

#### Scenario: Duplicate name returns 409
- **GIVEN** a nutrient "Carbohydrates" already exists
- **WHEN** an admin POSTs a nutrient with name "Carbohydrates"
- **THEN** the response is `409 Conflict`

### Requirement: Any authenticated user can list active nutrients

Nutrients with status ACTIVE are returned as a paginated, filterable, sortable list.

#### Scenario: User fetches first page
- **GIVEN** 25 active nutrients exist
- **WHEN** a user GETs `/v1/nutrients`
- **THEN** the response is `200 OK` with a page containing 20 nutrients (default page size)
- **AND** nutrients are sorted by name ascending by default
- **AND** the response includes pagination metadata and HATEOAS `_links`

#### Scenario: User filters by name
- **GIVEN** nutrients named "Protein", "Carbohydrates", "Vitamin C" exist
- **WHEN** a user GETs `/v1/nutrients?name=vit`
- **THEN** the response contains only "Vitamin C"

#### Scenario: User sorts by different field
- **GIVEN** multiple nutrients
- **WHEN** a user GETs `/v1/nutrients?sort=name,desc`
- **THEN** nutrients are returned in descending alphabetical order

### Requirement: Any authenticated user can get an active nutrient by id

#### Scenario: User fetches existing active nutrient
- **GIVEN** an active nutrient with id `abc-123` exists
- **WHEN** a user GETs `/v1/nutrients/abc-123`
- **THEN** the response is `200 OK`
- **AND** the body includes the nutrient with fine-grained HATEOAS `_links`

#### Scenario: User fetches non-existing nutrient
- **GIVEN** no nutrient with id `abc-123`
- **WHEN** a user GETs `/v1/nutrients/abc-123`
- **THEN** the response is `404 Not Found`

#### Scenario: User cannot fetch a suggested nutrient via nutrients endpoint
- **GIVEN** a suggested nutrient with id `abc-123` (status SUGGESTED)
- **WHEN** a user GETs `/v1/nutrients/abc-123`
- **THEN** the response is `404 Not Found`

### Requirement: Admin can update an active nutrient

#### Scenario: Admin updates name and kcal
- **GIVEN** an active nutrient with id `abc-123`
- **WHEN** an admin PUTs an updated payload to `/v1/nutrients/abc-123`
- **THEN** the response is `200 OK`
- **AND** the body reflects the updated values

#### Scenario: Admin updates non-existing nutrient
- **GIVEN** no nutrient with id `abc-123`
- **WHEN** an admin PUTs a payload to `/v1/nutrients/abc-123`
- **THEN** the response is `404 Not Found`

### Requirement: Admin can delete a nutrient

#### Scenario: Admin deletes an existing nutrient (any status)
- **GIVEN** a nutrient with id `abc-123` (any status) and associated votes
- **WHEN** an admin DELETEs `/v1/nutrients/abc-123`
- **THEN** the response is `204 No Content`
- **AND** associated votes are also removed (cascade)

#### Scenario: Admin deletes non-existing nutrient
- **GIVEN** no nutrient with id `abc-123`
- **WHEN** an admin DELETEs `/v1/nutrients/abc-123`
- **THEN** the response is `404 Not Found`
