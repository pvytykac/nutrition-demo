# Security Guide

This document explains the security model and authentication/authorization setup.

## Overview

We use OAuth2/JWT-based authentication with Keycloak as the identity provider.

## Architecture

```
Client (Browser/App) → API (Spring Boot) → Keycloak (Identity Provider)
                              ↓
                        JWT Token Validation
```

## Keycloak Integration

### Configuration

Keycloak runs in a Docker container (see `compose.yaml`):
- **URL**: http://localhost:8000
- **Realm**: nutrition
- **Admin Console**: http://localhost:8000/admin (admin/admin)

### Realm Roles

Roles are stored in Keycloak realm and mapped to JWT tokens:

- **admin** - Full access to all endpoints
- **user** - Limited access (for future use)

### JWT Token Structure

Keycloak issues tokens with roles in `realm_access.roles` claim:

```json
{
  "sub": "user-id",
  "preferred_username": "john",
  "realm_access": {
    "roles": ["admin", "user"]
  }
}
```

Spring Security converts these to `ROLE_admin` and `ROLE_user` authorities.

## Spring Security Configuration

### SecurityConfig

Located in `net.pvytykac.nutrition.common.security`:

- **OAuth2 Resource Server** - Validates JWT tokens
- **CORS** - Configured from `application.yml`
- **Stateless Sessions** - No server-side session
- **CSRF Disabled** - Not needed for stateless JWT

### Public Endpoints

These endpoints don't require authentication:
- `/swagger-ui/**` - API documentation UI
- `/v3/api-docs/**` - OpenAPI spec
- `/swagger-ui.html` - Swagger UI entry point

All other endpoints require a valid JWT token.

## Authorization Annotations

### @HasAdminRole

Requires `ROLE_admin` authority:

```java
@RestController
@HasAdminRole
public class IngredientController {
    // All endpoints require admin role
}
```

### @HasUserRole

Requires `ROLE_user` authority:

```java
@RestController
@HasUserRole
public class RecipeController {
    // All endpoints require user role
}
```

### Combined Usage

Apply at class level for all methods, or method level for specific endpoints:

```java
@RestController
@HasUserRole  // Default for all methods
public class MealController {
    
    @GetMapping
    public ResponseEntity<List<Meal>> getMeals() { }
    
    @PostMapping
    @HasAdminRole  // Override - only admins can create
    public ResponseEntity<Meal> createMeal() { }
}
```

## Testing with Authentication

In tests, use mock JWT tokens that simulate roles:

```java
// Admin role
withAdminAuth().get()
    .uri("/v1/ingredients")
    .exchange()
    .expectStatus().isOk();

// User role (will get 403 on admin endpoints)
withUserAuth().post()
    .uri("/v1/ingredients")
    .exchange()
    .expectStatus().isForbidden();
```

### TestJwtDecoderConfig

Converts token strings to mock JWTs:
- Token containing "admin" → ROLE_admin
- Token containing "user" → ROLE_user

## Securing a New Controller

1. Add security annotation at class level:

```java
@RestController
@RequestMapping("/v1/recipes")
@HasAdminRole
public class RecipeController { }
```

2. All endpoints now require authentication and the specified role

3. Write tests using `ControllerTestBase` with `withAdminAuth()`

## Token Acquisition (Development)

### Using Swagger UI

1. Navigate to http://localhost:8080/swagger-ui.html
2. Click "Authorize"
3. Login with Keycloak credentials:
   - Username: `admin`
   - Password: `admin`
4. Token is automatically used for all requests

### Using curl

```bash
# Get token from Keycloak
TOKEN=$(curl -X POST http://localhost:8000/realms/nutrition/protocol/openid-connect/token \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=password" \
  -d "client_id=ui" \
  -d "username=admin" \
  -d "password=admin" \
  | jq -r '.access_token')

# Use token in request
curl http://localhost:8080/v1/ingredients \
  -H "Authorization: Bearer $TOKEN"
```

## CORS Configuration

Configured in `application.yml`:

```yaml
cors:
  allowed-origins: http://localhost:9000,http://localhost:8080
```

Allowed methods: GET, POST, PUT, DELETE, OPTIONS

## Security Best Practices

1. **Always use HTTPS in production**
2. **Keep tokens short-lived** (Keycloak default: 5 min access, 30 min refresh)
3. **Validate issuer** (configured in application.yml)
4. **Use @HasAdminRole/@HasUserRole** instead of raw @PreAuthorize
5. **Test authorization** in controller tests
