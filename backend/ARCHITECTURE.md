# Architecture Guide

This document explains the architectural decisions and conventions used in this project.

## Package Structure Philosophy

We use a **Spring Modulith** structure where each module is a self-contained package with a public API surface and internal implementation hidden in an `internal/` subpackage.

```
net.pvytykac.nutrition/
├── NutritionDemoApplication.java     # @Modulithic entry point
├── OpenApiConfiguration.java
├── common/                           # shared cross-cutting (sharedModules)
│   ├── exceptions/
│   ├── filtering/
│   └── security/
├── ingredient/
│   ├── package-info.java             # @ApplicationModule
│   ├── IngredientLookup.java         # public NamedInterface
│   └── internal/
│       ├── Ingredient.java           # entity
│       ├── IngredientRepository.java # repository
│       ├── IngredientService.java    # service
│       ├── IngredientsController.java# controller
│       ├── IngredientRequestDTO.java # DTO
│       └── ...
├── nutrient/
│   ├── package-info.java
│   ├── NutrientLookup.java
│   └── internal/
│       ├── Nutrient.java
│       ├── ...
└── recipe/
    ├── package-info.java
    ├── RecipeLookup.java
    └── internal/
        ├── Recipe.java
        └── ...
```

### Why This Structure?

- **Encapsulation**: Implementation details are hidden in `internal/` — other modules can only depend on the public NamedInterface
- **Extractability**: Each module is designed so it could be extracted to a separate microservice by swapping the NamedInterface for an HTTP client
- **Module Focus**: Each top-level package represents a complete bounded context
- **Visibility Control**: `internal/` classes stay package-private; only the NamedInterface and DTOs are public

## Visibility Conventions

**Package-private by default** — only make classes public when they need to be accessed from other packages:

| Component | Visibility | Location | Reason |
|-----------|-----------|----------|--------|
| Controllers | `public` | `module/internal/` | Must be accessible by Spring |
| DTOs | `public` | `module/internal/` or `module/` | Cross-module serialization |
| NamedInterfaces | `public` | `module/` | Cross-module API surface |
| Services | package-private | `module/internal/` | Module-internal use |
| Repositories | package-private | `module/internal/` | Module-internal use |
| Entities | package-private | `module/internal/` | Module-internal use |

## Module Creation Checklist

When creating a new module under `net.pvytykac.nutrition.{moduleName}`:

1. **Create `package-info.java`** — annotate with `@ApplicationModule`
2. **Create `internal/` subpackage** — all implementation classes go here
3. **Entity** — JPA entity with UUID id, Lombok annotations (in `internal/`)
4. **Repository** — extends `JpaRepository` + `JpaSpecificationExecutor` (in `internal/`)
5. **Service** — `@Transactional`, manual DTO mapping (in `internal/`, package-private)
6. **Controller** — `@RestController`, `@RequestMapping("/v1/{plural}")` (in `internal/`)
7. **DTOs** — Request and Response DTOs with validation (in `internal/`)
8. **NamedInterface** — public interface at module root for cross-module access
9. **Filter** (optional) — if entity needs query parameter filtering (in `internal/`)
10. **Register in `@Modulithic`** if it needs to be a shared module (like `common`)
11. **Tests** — Service, Controller, and Repository tests

## Technology Stack

- **Java 25** — Latest LTS features
- **Spring Boot 4** — Framework
- **Spring Modulith** — Modular monolith structure with `@ApplicationModule`, `@Modulithic`, and transactional events
- **PostgreSQL 18** — Database
- **JPA/Hibernate** — ORM with JPA ModelGen for type-safe queries
- **Liquibase** — Database migrations
- **Lombok** — Boilerplate reduction
- **OpenAPI/Swagger** — API documentation
- **OAuth2/JWT** — Authentication via Keycloak
- **Testcontainers** — Integration testing

## Design Patterns

### Entity Pattern

```java
@Entity
@Table(name = "entities")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    // ... fields
}
```

### Repository Pattern

```java
@Repository
interface EntityRepository extends JpaRepository<Entity, UUID>, JpaSpecificationExecutor<Entity> {
    // Use findByIdForUpdate for updates (pessimistic locking)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Entity e WHERE e.id = :id")
    Optional<Entity> findByIdForUpdate(@Param("id") UUID id);
}
```

### Service Pattern

```java
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class EntityService {
    private final EntityRepository repository;
    
    // Business logic here
    // Manual DTO mapping (no MapStruct)
}
```

### Controller Pattern

Controllers live in the `internal/` subpackage of each module:

```java
@Slf4j
@RestController
@RequestMapping("/v1/entities")
@RequiredArgsConstructor
@Tag(name = "Entities")
@HasAdminRole  // or @HasUserRole, @HasUserOrAdminRole
public class EntitiesController {
    // CRUD endpoints
}
```

## Key Principles

1. **Immutability in DTOs** - Use `@Builder` and keep fields private
2. **Explicit Mappings** - Manual DTO-to-Entity conversion (no auto-mapping libraries)
3. **Type Safety** - Use JPA ModelGen metamodel classes for Criteria API queries
4. **Fail Fast** - Validation annotations on Request DTOs
5. **Consistent Error Handling** - Use `ResourceNotFoundException` for 404s
