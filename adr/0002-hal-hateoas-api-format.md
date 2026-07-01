# ADR-0002: HAL HATEOAS for REST API

## Status

Accepted

## Date

2026-07-01

## Context

The frontend needs to discover available actions based on the authenticated user's role (admin vs user) without hardcoding role checks. The API needs a standard way to communicate what operations are available on a resource and at a collection level.

Without HATEOAS, the frontend would need to either duplicate the backend's role logic or call a separate capabilities endpoint — both adding coupling and maintenance burden.

## Decision

Use the HAL (`application/hal+json`) specification for all REST API responses. Collections include coarse links (e.g., `create-nutrient`, `suggest-nutrient`) indicating what the caller can do at that scope. Individual resources include fine-grained links (e.g., `edit`, `delete`, `vote`, `approve`) for actions available on that specific resource. Links are conditionally included based on the authenticated user's roles.

Spring HATEOAS provides native HAL support (`RepresentationModel`, `EntityModel`, `CollectionModel`, `WebMvcLinkBuilder`) and integrates with Spring Boot 4.

## Consequences

- Good: Frontend can render UI based on link presence — no role logic duplication.
- Good: API is self-documenting and discoverable.
- Good: Adding new capabilities in the future is additive (new links) without breaking existing clients.
- Bad: Response payloads are larger due to link metadata.
- Bad: Increases per-endpoint implementation effort (must build link assemblies).
- Good: Future API changes can be versioned through link relations rather than URL paths.
