# Nutrition Demo Backend

## Overview
Spring Boot REST API for managing nutrition data.

## Project Description
REST APIs allowing users to manage lists of ingredients and recipes with the goal of keeping track of their daily nutrition details. 
The backend can also provide next-meal suggestions to users based on the meals they had already logged for the day and their nutrition goals.
We keep track of daily intake of fats, carbs and protein, including individual amino acids, especially phenylalanine (PKU).

## Package Structure

**Spring Modulith** modular monolith. Each module is a package under `net.pvytykac.nutrition`:

```
net.pvytykac.nutrition/
├── NutritionDemoApplication.java
├── OpenApiConfiguration.java
├── common/           # shared cross-cutting concerns
│   ├── exceptions/
│   ├── filtering/
│   └── security/
├── ingredient/
│   ├── (public NamedInterface)
│   └── internal/     # entity, repository, service, controller
├── nutrient/
│   ├── (public NamedInterface)
│   └── internal/
└── recipe/
    ├── (public NamedInterface)
    └── internal/
```

Each module exposes a **public NamedInterface** for cross-module API calls. Implementation classes (entities, repositories, services, controllers) live in an `internal/` subpackage and are package-private. The `common` module is declared as a shared module via `@Modulithic(sharedModules = {"common"})`.

Global beans, shared helpers and generic reusable classes live under `net.pvytykac.nutrition.common`.

See [MODULES.md](MODULES.md) for the full module catalog and [ARCHITECTURE.md](ARCHITECTURE.md) for detailed conventions.

## Technology Stack
- Java 25
- Spring Boot 4
- JPA + Hibernate
- PostgreSQL 18
- Liquibase for database migrations

## Technologies Used
* Spring Modulith — modular monolith structure with `@ApplicationModule`, `@Modulithic`, and transactional events
* PostgreSQL 18 for persistence
* Hibernate + JPA as ORM
* Liquibase for database migrations
* NamedInterfaces — typed in-process module APIs designed for eventual extraction to HTTP clients
* Spring Modulith transactional events for reliable cross-module communication
* Docker compose — `compose.yaml` declares all external services (PostgreSQL, Keycloak)

## Running
```bash
./mvnw spring-boot:run
```

## Testing
* Repository classes - all custom repository methods get tested using the `@DataJpaTest` test slice. These tests run against a PostgreSQL testcontainer. `TestEntityManager` is used to setup test data. Liquibase data migrations are disabled, instead test schema is created using Hibernate by overriding the ddl setting.
* Service classes - standard unit test, covering all lines and branches, including error cases like exceptions raised
* Controller - all HTTP methods tested using the `@WebMvcTest` test slice, the service layer is mocked. The tests cover payload validation, response codes including error codes, response payloads.
* Response/Request representation classes - get tested to verify that JSON gets properly (de)serialized
* All the other classes get unit tested just like service classes

## REST APIs
Resource names are plural and the paths are versioned, there's no root context path. 
Example: in case of the "ingredient" resource the path would be `/v1/ingredients(/{ingredientId})`

Methods:
* `POST /resources` - creates a new resource and responds with 201 Created with the representation of the created resource in the response payload on success. On error, responds with 400 Bad Request if the request payload was invalid, or with 409 if the same resource already exists. When specified, query parameters will be used to filter resources matching the request. 
* `GET /resources` - provides paging (default page size 20), the response payload always being an object with "content" field containing the list of items
* `GET /resources/{resourceId}` - provides representation of a single resource if it exists, responds with 404 otherwise
* `PUT /resources/{resourceId}` - fully updates the resource to match the provided representation if the resource exists, responds with 404 otherwise
* `DELETE /resources/{resourceId}` - deletes the resource if it exists and responds with 204 No Content, returns 404 otherwise

## Query Parameter Filtering
* String - query parameters - `field.value` - string to use for filtering, `field.operator` - one of (EQUALS, STARTS_WITH, ENDS_WITH, CONTAINS)
* Enums - query parameters - `field.value` - set of enum items to use for filtering, `field.operator` - one of (IN, NOT_IN)
* Numbers - query parameters - `field.value` - set of numbers to use for filtering, `field.operator` - one of (EQUALS, GREATER_THAN, GREATER_THAN_OR_EQUAL, LOWER_THAN, LOWER_THAN_OR_EQUAL, BETWEEN)

## Entity IDs
All JPA entity identifiers use **UUID** format (e.g., `550e8400-e29b-41d4-a716-446655440000`).
