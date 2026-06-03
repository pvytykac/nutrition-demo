# exceptions

Base exception hierarchy and global error handling.

## Public API

### ApplicationException
Abstract base class for all application exceptions.

### ResourceNotFoundException
Thrown when a requested resource doesn't exist. Mapped to HTTP 404 by `GlobalExceptionHandler`.

```java
throw new ResourceNotFoundException("Ingredient", id);
```

## Internal

### GlobalExceptionHandler
`@RestControllerAdvice` that handles exceptions globally:
- `ResourceNotFoundException` → 404 Not Found
- `DataIntegrityViolationException` → 409 Conflict
- Returns RFC 7807 `ProblemDetail` responses

