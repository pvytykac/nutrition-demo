# Module Architecture

## Overview

Modular monolith designed for eventual extraction to microservices. Each module is a self-contained package with its own entities, services, and database tables. Cross-module communication happens via **Spring Modulith transactional events** and **NamedInterfaces** (in-process, swappable to HTTP).

| Module | Responsibility | REST Prefix |
|---|---|---|
| **user** | User profiles, roles (USER/ADMIN), social login | `/v1/users` |
| **nutritional-detail** | Reference nutrient definitions (Protein, Phenylalanine, etc.) | `/v1/nutritional-details` |
| **ingredient** | Pantry items + nutritional facts per 100g, voting | `/v1/ingredients` |
| **recipe** | Recipes (ingredient compositions), voting | `/v1/recipes` |
| **nutrition-log** | Daily food intake logging, pre-computed nutrition snapshots | `/v1/log` |
| **request** | Nutrient requests, edit requests, inaccuracy flags | `/v1/requests` |
| **insight** | Analytics on logged nutrition (TBD — skipped for MVP) | `/v1/insights` |

Shared cross-cutting concerns live in `net.pvytykac.nutrition.common`.

---

## Package Structure

```
net.pvytykac.nutrition
├── NutritionDemoApplication.java
├── OpenApiConfig.java
├── common/                          # shared cross-cutting concerns
│   ├── exceptions/
│   ├── filtering/
│   └── security/
├── user/
│   ├── UserController.java          # public REST endpoints
│   ├── UserLookup.java              # NamedInterface for other modules
│   └── internal/
│       ├── User.java                # JPA entity
│       ├── UserRepository.java
│       └── UserService.java
├── nutritionaldetail/
│   ├── NutritionalDetailController.java
│   ├── NutritionalDetailLookup.java
│   └── internal/
│       ├── NutritionalDetail.java
│       ├── NutritionalDetailRepository.java
│       └── NutritionalDetailService.java
├── ingredient/
│   ├── IngredientController.java
│   ├── IngredientLookup.java
│   └── internal/
│       ├── Ingredient.java
│       ├── IngredientNutrition.java
│       ├── IngredientVote.java
│       ├── IngredientRepository.java
│       ├── IngredientService.java
│       ├── IngredientVoteService.java
│       └── VerificationService.java
├── recipe/
│   ├── RecipeController.java
│   ├── RecipeLookup.java
│   └── internal/
│       ├── Recipe.java
│       ├── RecipeIngredient.java
│       ├── RecipeNutrition.java
│       ├── RecipeVote.java
│       ├── RecipeRepository.java
│       ├── RecipeService.java
│       ├── RecipeVoteService.java
│       └── VerificationService.java
├── nutritionlog/
│   ├── NutritionLogController.java
│   ├── NutritionLogLookup.java
│   └── internal/
│       ├── NutritionLogEntry.java
│       ├── LogEntryNutrition.java
│       ├── NutritionLogRepository.java
│       └── NutritionLogService.java
└── request/
    ├── RequestController.java
    └── internal/
        ├── NutritionalDetailRequest.java
        ├── EditRequest.java
        ├── InaccuracyFlag.java
        ├── RequestRepository.java
        └── RequestService.java
```

---

## Database Schema (shared DB, separate tables per module)

### user

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    roles VARCHAR(50)[] NOT NULL DEFAULT '{USER}',
    social_provider VARCHAR(50),
    social_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE UNIQUE INDEX idx_users_social ON users(social_provider, social_id);
CREATE UNIQUE INDEX idx_users_username ON users(username);
```

### nutritional_detail

```sql
CREATE TABLE nutritional_details (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,      -- e.g. 'PROTEIN'
    name VARCHAR(255) NOT NULL,            -- e.g. 'Protein'
    measurement_unit VARCHAR(10) NOT NULL, -- e.g. 'G'
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

### ingredient

```sql
CREATE TABLE ingredients (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',  -- PRIVATE, PUBLIC_UNVERIFIED, VERIFIED, DELETED
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE ingredient_nutrition (
    ingredient_id UUID NOT NULL REFERENCES ingredients(id),
    nutritional_detail_id UUID NOT NULL,           -- no FK (nutritional-detail module's data)
    amount_per_100g DECIMAL(19, 4) NOT NULL,
    PRIMARY KEY (ingredient_id, nutritional_detail_id)
);

CREATE TABLE ingredient_votes (
    id UUID PRIMARY KEY,
    ingredient_id UUID NOT NULL REFERENCES ingredients(id),
    user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_ingredient_vote_per_day UNIQUE (ingredient_id, user_id, created_at::date)
);
```

### recipe

```sql
CREATE TABLE recipes (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    servings INT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PRIVATE',  -- PRIVATE, PUBLIC_UNVERIFIED, VERIFIED, DELETED
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE recipe_ingredients (
    recipe_id UUID NOT NULL REFERENCES recipes(id),
    ingredient_id UUID NOT NULL,                   -- no FK (ingredient module's data)
    amount DECIMAL(19, 4) NOT NULL,
    unit VARCHAR(10) NOT NULL,                     -- G, ML, UNIT
    PRIMARY KEY (recipe_id, ingredient_id, unit)
);

CREATE TABLE recipe_nutrition (
    recipe_id UUID NOT NULL REFERENCES recipes(id),
    nutritional_detail_id UUID NOT NULL,           -- no FK (nutritional-detail module's data)
    amount_per_serving DECIMAL(19, 4) NOT NULL,
    PRIMARY KEY (recipe_id, nutritional_detail_id)
);

CREATE TABLE recipe_votes (
    id UUID PRIMARY KEY,
    recipe_id UUID NOT NULL REFERENCES recipes(id),
    user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_recipe_vote_per_day UNIQUE (recipe_id, user_id, created_at::date)
);
```

### nutrition_log

```sql
CREATE TABLE nutrition_log_entries (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    date DATE NOT NULL,
    source_type VARCHAR(10) NOT NULL,              -- INGREDIENT, RECIPE
    source_id UUID NOT NULL,                       -- no FK
    amount DECIMAL(19, 4) NOT NULL,
    unit VARCHAR(10) NOT NULL,                     -- G, ML, UNIT, PORTION
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_log_user_date ON nutrition_log_entries(user_id, date);

CREATE TABLE log_entry_nutrition (
    log_entry_id UUID NOT NULL REFERENCES nutrition_log_entries(id),
    nutritional_detail_id UUID NOT NULL,           -- no FK
    amount DECIMAL(19, 4) NOT NULL,
    PRIMARY KEY (log_entry_id, nutritional_detail_id)
);
```

### request

```sql
CREATE TABLE nutritional_detail_requests (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    name VARCHAR(255) NOT NULL,
    measurement_unit VARCHAR(10) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    reviewed_by UUID REFERENCES users(id),
    reviewed_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE edit_requests (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    target_type VARCHAR(20) NOT NULL,              -- INGREDIENT, RECIPE
    target_id UUID NOT NULL,
    proposed_changes JSONB NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    reviewed_by UUID REFERENCES users(id),
    reviewed_at TIMESTAMP,
    rejection_reason TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE inaccuracy_flags (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    target_type VARCHAR(20) NOT NULL,              -- INGREDIENT, RECIPE
    target_id UUID NOT NULL,
    description TEXT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'OPEN',   -- OPEN, RESOLVED, DISMISSED
    resolved_by UUID REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

---

## NamedInterfaces

In-process module APIs designed with signatures that an HTTP client could replace at extraction time.

### UserLookup

```
UserDTO findById(UUID id)
    throws ResourceNotFoundException

boolean hasRole(UUID userId, Role role)
```

### NutritionalDetailLookup

```
List<NutritionalDetailDTO> findAllByIds(Collection<UUID> ids)
```

### IngredientLookup

```
List<IngredientSummaryDTO> findAllByIds(Collection<UUID> ids)
    — returns id, name, nutrition[ { detailId, amountPer100g } ]

boolean existsById(UUID id)
```

### RecipeLookup

```
RecipeSummaryDTO findById(UUID id)
    — returns id, name, servings, nutrition[ { detailId, amountPerServing } ]
```

### NutritionLogLookup

```
List<LogEntryDTO> findByUserAndDateRange(UUID userId, LocalDate from, LocalDate to)
```

---

## Events

Events are published via `ApplicationEventPublisher` and stored in `application_events` table via Spring Modulith for reliable delivery.

| Event | Publisher | Consumers | Payload |
|---|---|---|---|
| `UserRegistered` | user | (future: notifications) | userId |
| `NutritionalDetailCreated` | nutritional-detail | (future: search indexing) | detailId, code, name |
| `IngredientCreated` | ingredient | request (if from approved edit request) | ingredientId, createdBy |
| `IngredientVerified` | ingredient | recipe (flag stale recipes), request (fulfill edit request) | ingredientId |
| `IngredientSoftDeleted` | ingredient | recipe (flag recipes using this ingredient) | ingredientId |
| `IngredientUpdated` | ingredient | recipe (nutrition changed, flag recipes) | ingredientId |
| `RecipeCreated` | recipe | — | recipeId, createdBy |
| `RecipeVerified` | recipe | — | recipeId |
| `RecipeSoftDeleted` | recipe | — | recipeId |
| `LogEntryCreated` | nutrition-log | (future: insight) | logEntryId, userId, date |
| `LogEntryDeleted` | nutrition-log | (future: insight) | logEntryId, userId, date |
| `LogEntryUpdated` | nutrition-log | (future: insight) | logEntryId, userId, date |
| `NutritionalDetailRequested` | request | — | requestId, userId, name |
| `NutritionalDetailRequestApproved` | request | — | requestId, detailId |
| `NutritionalDetailRequestRejected` | request | — | requestId, reason |
| `EditRequested` | request | — | requestId, targetType, targetId |
| `EditRequestApproved` | request | ingredient/recipe | requestId, targetType, targetId, proposedChanges |
| `EditRequestRejected` | request | — | requestId, reason |
| `InaccuracyFlagged` | request | — | flagId, targetType, targetId |

---

## Key Flows

### Recipe Creation

```
POST /v1/recipes {ingredients: [{id, amount, unit}], servings, name, ...}
  → RecipeController → RecipeService (@Transactional)
  → IngredientLookup.findAllByIds(ids)       -- validates existence, returns nutrition
  → pre-compute per-serving nutrition
  → save Recipe + RecipeIngredients + RecipeNutrition
  → publish RecipeCreated (transactional event)
  → return 201 + RecipeResponseDTO
```

### Voting → Auto-Verification

```
POST /v1/ingredients/{id}/vote
  → IngredientController → IngredientVoteService (@Transactional)
  → check daily limit: count votes WHERE ingredient_id=? AND user_id=? AND created_at::date=TODAY
  → if >= 10, reject with 429
  → save vote
  → count total votes for ingredient
  → if count >= 10 AND status = PUBLIC_UNVERIFIED
      → status = VERIFIED
      → publish IngredientVerified
  → return 201
```

Recipe module consumes `IngredientVerified`:
```
→ find all PUBLIC_UNVERIFIED/VERIFIED recipes using this ingredient
→ update status to STALE or flag for user review
```

### Nutrition Logging

```
POST /v1/log {sourceType: "RECIPE", sourceId, amount: 2, unit: "PORTION", date}
  → LogController → NutritionLogService (@Transactional)
  → if sourceType = RECIPE: RecipeLookup.findById(sourceId) → per-serving nutrition
  → if sourceType = INGREDIENT: IngredientLookup.findById(sourceId) → per-100g nutrition
  → multiply by amount → compute log entry nutrition
  → save NutritionLogEntry + LogEntryNutrition
  → publish LogEntryCreated
  → return 201
```

### Nutritional Detail Request

```
User: POST /v1/requests/nutritional-details {name, measurementUnit, description}
  → save NutritionalDetailRequest (status = PENDING)
  → publish NutritionalDetailRequested

Admin: POST /v1/requests/nutritional-details/{id}/approve
  → RequestService (@Transactional)
  → create NutritionalDetail via NutritionalDetailService
  → status = APPROVED, reviewed_by, reviewed_at
  → publish NutritionalDetailCreated + NutritionalDetailRequestApproved

Admin: POST /v1/requests/nutritional-details/{id}/reject {reason}
  → status = REJECTED, rejection_reason
  → publish NutritionalDetailRequestRejected
```

---

## Entity Statuses

### Ingredient / Recipe

| Status | Description |
|---|---|
| `PRIVATE` | Only the creator can see it. |
| `PUBLIC_UNVERIFIED` | Visible to all, eligible for voting. |
| `VERIFIED` | Reached 10 upvotes or admin-verified. |
| `DELETED` | Soft-deleted (keeps references intact). |

### Request Statuses

| Status | Description |
|---|---|
| `PENDING` | Awaiting admin review. |
| `APPROVED` | Admin approved (and action was taken). |
| `REJECTED` | Admin rejected with reason. |

### Inaccuracy Flag Statuses

| Status | Description |
|---|---|
| `OPEN` | Submitted, awaiting resolution. |
| `RESOLVED` | Admin fixed the data. |
| `DISMISSED` | Admin determined it's not an issue. |

---

## REST Endpoints

### User (`/v1/users`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `POST` | `/v1/users` | ADMIN | Create user |
| `GET` | `/v1/users/me` | USER | Current user profile |

### Nutritional Detail (`/v1/nutritional-details`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/v1/nutritional-details` | any | List all nutrients |
| `GET` | `/v1/nutritional-details/{id}` | any | Get single nutrient |
| `POST` | `/v1/nutritional-details` | ADMIN | Create nutrient |

### Ingredient (`/v1/ingredients`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/v1/ingredients` | any | List (paginated, filterable) |
| `POST` | `/v1/ingredients` | USER | Create |
| `GET` | `/v1/ingredients/{id}` | any | Get + nutrition |
| `PUT` | `/v1/ingredients/{id}` | owner or ADMIN | Update |
| `DELETE` | `/v1/ingredients/{id}` | owner or ADMIN | Soft delete |
| `POST` | `/v1/ingredients/{id}/vote` | USER | Upvote |
| `POST` | `/v1/ingredients/{id}/verify` | ADMIN | Admin-verify |

### Recipe (`/v1/recipes`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/v1/recipes` | any | List (paginated, filterable) |
| `POST` | `/v1/recipes` | USER | Create |
| `GET` | `/v1/recipes/{id}` | any | Get + pre-computed nutrition |
| `PUT` | `/v1/recipes/{id}` | owner or ADMIN | Update |
| `DELETE` | `/v1/recipes/{id}` | owner or ADMIN | Soft delete |
| `POST` | `/v1/recipes/{id}/vote` | USER | Upvote |
| `POST` | `/v1/recipes/{id}/verify` | ADMIN | Admin-verify |

### Nutrition Log (`/v1/log`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/v1/log` | USER | List own logs (date range filter) |
| `POST` | `/v1/log` | USER | Add entry |
| `PUT` | `/v1/log/{id}` | owner | Update entry |
| `DELETE` | `/v1/log/{id}` | owner | Delete entry |

### Request (`/v1/requests`)

| Method | Path | Auth | Description |
|---|---|---|---|
| `GET` | `/v1/requests/nutritional-details` | ADMIN | List pending requests |
| `POST` | `/v1/requests/nutritional-details` | USER | Create request |
| `POST` | `/v1/requests/nutritional-details/{id}/approve` | ADMIN | Approve + create detail |
| `POST` | `/v1/requests/nutritional-details/{id}/reject` | ADMIN | Reject with reason |
| `GET` | `/v1/requests/edits` | ADMIN | List edit requests |
| `POST` | `/v1/requests/edits` | USER | Propose edit to ingredient/recipe |
| `POST` | `/v1/requests/edits/{id}/approve` | ADMIN | Approve edit |
| `POST` | `/v1/requests/edits/{id}/reject` | ADMIN | Reject edit |
| `GET` | `/v1/requests/flags` | ADMIN | List inaccuracy flags |
| `POST` | `/v1/requests/flags` | USER | Flag inaccuracy |
| `POST` | `/v1/requests/flags/{id}/resolve` | ADMIN | Resolve flag |
| `POST` | `/v1/requests/flags/{id}/dismiss` | ADMIN | Dismiss flag |

---

## Design Decisions

1. **Ingredient IDs in recipes** — stored without FK constraint. The NamedInterface validates existence at recipe creation time. Soft-delete means IDs never truly disappear, keeping recipe references intact.

2. **Voting duplication** — vote entities and daily-limit logic are duplicated in ingredient and recipe modules. If this becomes cumbersome, a shared `Votable` abstraction can be extracted into `common`.

3. **IngredientUpdated → recipe staleness** — when ingredient nutrition data changes, recipes using it are flagged (not auto-updated). The user reviews and refreshes them manually. This avoids cascading changes.

4. **Nutrition snapshots** — both recipes and log entries store pre-computed nutrition values at creation time. This makes reads fast and avoids recalculation. Recipes can be refreshed manually; log entries are historical snapshots and never change.

5. **No FKs to other modules** — tables reference IDs from other modules without FK constraints. This avoids cross-module referential integrity and simplifies eventual extraction.

6. **Soft delete** — ingredients and recipes use soft-delete (`DELETED` status) rather than physical deletion. This preserves historical references in recipes and log entries.
