# AGENTS.md

Guide for AI agents working on this nutrition tracking application.

## Project Overview

Multimodule project with a Spring Boot 4 backend. The backend is a REST API for managing nutrition data with PKU (phenylalanine) tracking support.

## Modules

- **backend** - Java 25 + Spring Boot 4 REST API - see @backend/README.md
- **frontend** - TypeScript + React SPA (planned) - see @frontend/README.md
- **e2e-tests** - TypeScript + Playwright tests (planned) - see @e2e-tests/README.md

## Backend Build Commands

All commands run from `/home/paly/projects/nutrition-demo/backend` directory:

```bash
# Build the project
./mvnw clean compile

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=IngredientServiceTest

# Run a single test method
./mvnw test -Dtest=IngredientServiceTest#shouldCreateIngredientSuccessfully

# Run tests matching a pattern
./mvnw test -Dtest="*ControllerTest"

# Package application
./mvnw clean package

# Run application
./mvnw spring-boot:run

# Generate test coverage report (JaCoCo)
./mvnw jacoco:report
```

## Infrastructure

```bash
# Start PostgreSQL and Keycloak (from backend/)
docker compose up -d

# View logs
docker compose logs -f

# Stop services
docker compose down
```

## Code Style Guidelines

### Java Style

**Imports**: Group by: 1) Java standard library, 2) Third-party libraries, 3) Project imports. No wildcard imports.

**Formatting**: Use standard Java conventions. Class members: static fields, instance fields, constructor, methods. Use Lombok annotations: `@Getter`, `@Setter`, `@Builder`, `@RequiredArgsConstructor`.

**Types**: All entity IDs use UUID. Use `BigDecimal` for monetary/nutritional values. Use `var` for local variables when type is obvious.

**Naming**:
- Classes: PascalCase (`IngredientService`, `NutritionDetailsRequestDTO`)
- Methods: camelCase (`createIngredient`, `getIngredientById`)
- Constants: UPPER_SNAKE_CASE
- Package-private by default; only make public what's needed externally

**Error Handling**: Use `ResourceNotFoundException` for 404s. Controllers return appropriate HTTP status codes (201 Created, 204 No Content). Use `@Valid` for request validation.

### Package Structure

Flat package structure under `net.pvytykac.nutrition`. Each module has its own package containing entity, repository, service, controller, and DTO classes. Global/shared classes under `net.pvytykac.nutrition.common`.

### REST API Conventions

- Resource paths: `/v1/{pluralResource}` (e.g., `/v1/ingredients`)
- POST /resources - 201 Created with body
- GET /resources - paginated (default 20 items), returns `{content: [], page: {...}}`
- GET /resources/{id} - 200 or 404
- PUT /resources/{id} - full update, 200 or 404
- DELETE /resources/{id} - 204 No Content or 404

### Testing Standards

**Service Tests**: Use `@ExtendWith(MockitoExtension.class)`. Mock dependencies with `@Mock`. Cover all branches including error cases.

**Controller Tests**: Extend `ControllerTestBase`. Use `@WebMvcTest`. Mock service layer with `@MockitoBean`. Test validation, status codes, response payloads.

**Repository Tests**: Extend `RepositoryTestBase`. Uses Testcontainers PostgreSQL. Use `TestEntityManager` for test data setup.

**Test Structure**: Use `@Nested` classes grouped by method under test. Test names: `should{ExpectedBehavior}When{Condition}`.

## Technology Stack

- Java 25
- Spring Boot 4.0.3
- PostgreSQL 18 + Testcontainers
- JPA/Hibernate with JPA ModelGen
- Liquibase for migrations
- Lombok
- OpenAPI/Swagger
- OAuth2/JWT (Keycloak)
- JUnit 5 + Mockito + AssertJ
