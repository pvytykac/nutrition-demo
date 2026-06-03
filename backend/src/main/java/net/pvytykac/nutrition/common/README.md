# Common Package

Cross-cutting shared code used by multiple application modules.

## Subpackages

| Package | Responsibility |
|---|---|
| [exceptions/](exceptions/README.md) | Exception hierarchy and global error handling |
| [filtering/](filtering/README.md) | Specification-based JPA filtering system |
| [security/](security/README.md) | OAuth2/JWT security configuration and authorization annotations |

## When to Add to Common

Add classes here when they are:
- Used by multiple modules
- Generic and reusable
- Related to cross-cutting concerns (security, error handling)
- Part of shared infrastructure

**Don't add** module-specific logic here — keep it in the module's own package.

