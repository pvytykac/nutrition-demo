# filtering

Generic specification-based filtering system for JPA queries.

## Key Classes

- **SpecificationBuilder** — Creates JPA `Specification` objects from filter values
- **StringFilter** — String field matching: `EXACT_MATCH`, `STARTS_WITH`, `ENDS_WITH`, `CONTAINS`, `IN`
- **NumericFilter** — Numeric field matching: `EQUAL`, `GREATER_THAN`, `LOWER_THAN`, `BETWEEN`, etc.
- **EnumFilter** — Enum field matching: `IN`, `NOT_IN`

See [FILTERING.md](../../../../../FILTERING.md) for detailed documentation on usage and adding filtering to new entities.
