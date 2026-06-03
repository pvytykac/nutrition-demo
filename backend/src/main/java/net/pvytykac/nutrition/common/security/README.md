# security

Security configuration and authorization annotations.

## Public API

### Annotations

| Annotation | Authority required |
|---|---|
| `@HasAdminRole` | `admin` |
| `@HasUserRole` | `user` |
| `@HasUserOrAdminRole` | `user` or `admin` |

Apply at class or method level on controllers:

```java
@RestController
@RequestMapping("/v1/ingredients")
@HasAdminRole
public class IngredientsController { }
```

## Internal

### SecurityConfiguration
Spring Security configuration: OAuth2 resource server with JWT, CORS, stateless sessions, method security.

### JwtConverter
Extracts roles from Keycloak's `realm_access.roles` JWT claim and maps them to Spring Security authorities.

See [SECURITY.md](../../../../../SECURITY.md) for detailed documentation.
