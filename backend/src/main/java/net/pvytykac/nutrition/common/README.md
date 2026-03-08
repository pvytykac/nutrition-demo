# Common Package

This package contains shared/reusable components that are used across multiple modules.

## Structure

```
common/
├── exceptions/      # Global exception handling
├── filtering/       # Specification-based filtering system
└── security/        # Security configuration and annotations
```

## Exceptions

### ApplicationException
Base class for all application exceptions. Provides common fields for error handling.

### ResourceNotFoundException
Thrown when a requested resource doesn't exist. Results in HTTP 404 response.

```java
Ingredient ingredient = repository.findById(id)
    .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
```

### GlobalExceptionHandler
`@RestControllerAdvice` that handles exceptions globally:
- `ResourceNotFoundException` → 404 Not Found
- Validation errors → 400 Bad Request
- Returns RFC 7807 ProblemDetail responses

## Filtering

Generic specification-based filtering system for JPA queries.

See [FILTERING.md](../../FILTERING.md) for detailed documentation.

### Key Classes

- **SpecificationBuilder** - Creates JPA Specifications from filter objects
- **StringFilter** - String field filtering (EXACT_MATCH, STARTS_WITH, ENDS_WITH, CONTAINS, IN)
- **NumericFilter** - Numeric field filtering (EQUAL, GREATER_THAN, LOWER_THAN, BETWEEN, etc.)
- **EnumFilter** - Enum field filtering (IN, NOT_IN)

## Security

### SecurityConfig
Spring Security configuration:
- OAuth2 resource server with JWT
- CORS configuration
- Role-based access control

### Custom Annotations

- **@HasAdminRole** - Requires "admin" role
- **@HasUserRole** - Requires "user" role

Apply at class level on controllers:

```java
@RestController
@HasAdminRole
public class IngredientController { }
```

### JWT Token Structure

Tokens from Keycloak contain roles in `realm_access.roles` claim:

```json
{
  "realm_access": {
    "roles": ["admin", "user"]
  }
}
```

## When to Add to Common

Add classes here when they are:
- Used by multiple modules
- Generic and reusable
- Related to cross-cutting concerns (security, error handling)
- Part of shared infrastructure

**Don't add** module-specific logic here - keep it in the module's package.
