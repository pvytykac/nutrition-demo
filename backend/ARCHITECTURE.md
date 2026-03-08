# Architecture Guide

This document explains the architectural decisions and conventions used in this project.

## Package Structure Philosophy

We use a **flat package structure** where all classes related to a module live in a single package.

```
net.pvytykac.nutrition/
├── ingredient/           # All ingredient-related classes
│   ├── Ingredient.java              # Entity
│   ├── IngredientRepository.java    # Repository
│   ├── IngredientService.java       # Service
│   ├── IngredientController.java    # Controller
│   ├── IngredientRequestDTO.java    # Request DTO
│   ├── IngredientResponseDTO.java   # Response DTO
│   ├── IngredientFilter.java        # Filter logic
│   └── ...
└── common/               # Shared components
    ├── exceptions/
    ├── filtering/
    └── security/
```

### Why Flat Structure?

- **Encapsulation**: All related classes are co-located and can be package-private
- **Simplicity**: No need to navigate deep hierarchies
- **Module Focus**: Each package represents a complete feature/module
- **Visibility Control**: Package-private by default, public only when necessary

## Visibility Conventions

**Package-private by default** - only make classes public when they need to be accessed from other packages:

| Component | Visibility | Reason |
|-----------|-----------|---------|
| Controllers | `public` | Must be accessible by Spring |
| DTOs | `public` | Cross-package serialization |
| Services | package-private | Only used within module |
| Repositories | package-private | Only used within module |
| Entities | package-private | Only used within module |

## Module Creation Checklist

When creating a new module (e.g., `recipe`, `meal`, `user`):

1. **Create package** under `net.pvytykac.nutrition.{moduleName}`
2. **Entity** - JPA entity with UUID id, Lombok annotations
3. **Repository** - extends `JpaRepository` + `JpaSpecificationExecutor`
4. **Service** - package-private, `@Transactional`, manual DTO mapping
5. **Controller** - public, `@RestController`, `@RequestMapping("/v1/{plural}")`
6. **DTOs** - Request and Response DTOs with validation
7. **Filter** (optional) - if entity needs query parameter filtering
8. **Tests** - Service, Controller, and Repository tests

## Technology Stack

- **Java 25** - Latest LTS features
- **Spring Boot 4** - Framework
- **PostgreSQL 18** - Database
- **JPA/Hibernate** - ORM with JPA ModelGen for type-safe queries
- **Liquibase** - Database migrations
- **Lombok** - Boilerplate reduction
- **OpenAPI/Swagger** - API documentation
- **OAuth2/JWT** - Authentication via Keycloak
- **Testcontainers** - Integration testing

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

```java
@Slf4j
@RestController
@RequestMapping("/v1/entities")
@RequiredArgsConstructor
@Tag(name = "Entities")
@HasAdminRole  // or @HasUserRole
public class EntityController {
    // CRUD endpoints
}
```

## Key Principles

1. **Immutability in DTOs** - Use `@Builder` and keep fields private
2. **Explicit Mappings** - Manual DTO-to-Entity conversion (no auto-mapping libraries)
3. **Type Safety** - Use JPA ModelGen metamodel classes for Criteria API queries
4. **Fail Fast** - Validation annotations on Request DTOs
5. **Consistent Error Handling** - Use `ResourceNotFoundException` for 404s
