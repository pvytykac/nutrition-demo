# Filtering Guide

This document explains the specification-based filtering system used for querying entities with complex criteria.

## Overview

The filtering system allows clients to filter collections using query parameters. It uses a three-layer architecture:

```
HTTP Query Parameters → QueryParameters DTO → Filter Container → Specification → JPA Query
```

## Architecture Layers

### 1. QueryParameters (HTTP Binding)

Binds query parameters from the HTTP request to filter objects using Spring's `@ParameterObject`.

```java
@Data
public class IngredientsQueryParameters {
    StringFilter name;           // name.value=Apple&name.operator=CONTAINS
    UnitFilter unit;             // unit.value=GRAM&unit.operator=IN
    NumericFilter fatContent;    // fatContent.value=10&fatContent.operator=GREATER_THAN
    // ...
}
```

**Query Parameter Format:**
- `field.value` - the value(s) to filter by
- `field.operator` - the comparison operator

**Example:**
```
GET /v1/ingredients?name.value=Apple&name.operator=CONTAINS&fatContent.value=10&fatContent.operator=GREATER_THAN
```

### 2. Filter Types (Reusable Value Objects)

Three filter types are available in `net.pvytykac.nutrition.common.filtering`:

#### StringFilter
Operators: `EXACT_MATCH`, `STARTS_WITH`, `ENDS_WITH`, `CONTAINS`, `IN`

```java
StringFilter filter = new StringFilter();
filter.setValue(List.of("Apple"));
filter.setOperator(StringFilter.Operator.CONTAINS);
```

#### NumericFilter (BigDecimal)
Operators: `EQUAL`, `GREATER_THAN`, `LOWER_THAN`, `GREATER_THAN_OR_EQUAL`, `LOWER_THAN_OR_EQUAL`, `BETWEEN`

```java
NumericFilter filter = new NumericFilter();
filter.setValue(List.of(new BigDecimal("10.0"), new BigDecimal("20.0")));
filter.setOperator(NumericFilter.Operator.BETWEEN);
```

#### EnumFilter
Operators: `IN`, `NOT_IN`

```java
// Extend EnumFilter for specific enum types
public class UnitFilter extends EnumFilter<Unit> {}
```

### 3. Filter Container (Entity-Specific)

A container class that knows which filters apply to a specific entity and builds the combined specification.

```java
@Getter
@Builder
public class IngredientFilter {
    private final StringFilter nameFilter;
    private final EnumFilter<Unit> unitFilter;
    private final NumericFilter fatContentFilter;
    // ...
    
    public Specification<Ingredient> toSpecification() {
        return SpecificationBuilder.combine(
            nameSpecification(),
            unitSpecification(),
            // ...
        );
    }
}
```

Key responsibilities:
- Define which filters apply to the entity
- Provide field expressions using JPA ModelGen metamodel classes (`Ingredient_.NAME`)
- Combine individual specifications with AND logic

### 4. SpecificationBuilder (Generic Builder)

Creates JPA Specifications from filter objects. Located in `net.pvytykac.nutrition.common.filtering`.

```java
// String filtering
Specification<T> stringSpec = SpecificationBuilder.stringFilter(
    filter,
    root -> root.get(Entity_.FIELD_NAME)
);

// Numeric filtering  
Specification<T> numericSpec = SpecificationBuilder.numericFilter(
    filter,
    root -> root.get(Entity_.FIELD_NAME)
);

// Enum filtering
Specification<T> enumSpec = SpecificationBuilder.enumFilter(
    filter,
    root -> root.get(Entity_.FIELD_NAME)
);

// Combine with AND
Specification<T> combined = SpecificationBuilder.combine(spec1, spec2, spec3);
```

## Adding Filtering to a New Entity

Follow these steps to add query parameter filtering to a new entity:

### 1. Create QueryParameters Class

```java
@Data
public class RecipeQueryParameters {
    StringFilter name;
    NumericFilter totalCalories;
    EnumFilter<Difficulty> difficulty;
}
```

### 2. Create Filter Container

```java
@Getter
@Builder
public class RecipeFilter {
    private final StringFilter nameFilter;
    private final NumericFilter totalCaloriesFilter;
    private final EnumFilter<Difficulty> difficultyFilter;
    
    public Specification<Recipe> toSpecification() {
        return SpecificationBuilder.combine(
            nameSpecification(),
            totalCaloriesSpecification(),
            difficultySpecification()
        );
    }
    
    private Specification<Recipe> nameSpecification() {
        if (!isNameFilterActive()) return null;
        return SpecificationBuilder.stringFilter(
            nameFilter,
            root -> root.get(Recipe_.NAME)
        );
    }
    
    private Specification<Recipe> totalCaloriesSpecification() {
        if (!isTotalCaloriesFilterActive()) return null;
        return SpecificationBuilder.numericFilter(
            totalCaloriesFilter,
            root -> root.get(Recipe_.TOTAL_CALORIES)
        );
    }
    
    // ... add helper methods for checking if filters are active
    public boolean isNameFilterActive() {
        return nameFilter != null && nameFilter.isActive();
    }
}
```

### 3. Wire Up in Controller

```java
@GetMapping
public ResponseEntity<Page<RecipeResponseDTO>> getRecipes(
        @ParameterObject RecipeQueryParameters queryParameters,
        @ParameterObject Pageable pageable) {
    
    var filter = RecipeFilter.builder()
        .nameFilter(queryParameters.getName())
        .totalCaloriesFilter(queryParameters.getTotalCalories())
        .build();
    
    Page<RecipeResponseDTO> page = recipeService.searchRecipes(filter, pageable);
    return ResponseEntity.ok(page);
}
```

### 4. Add Repository Method

```java
@Repository
interface RecipeRepository extends JpaRepository<Recipe, UUID>, JpaSpecificationExecutor<Recipe> {
    // JpaSpecificationExecutor provides findAll(Specification, Pageable)
}
```

### 5. Add Service Method

```java
public Page<RecipeResponseDTO> searchRecipes(RecipeFilter filter, Pageable pageable) {
    Specification<Recipe> spec = filter.toSpecification();
    Page<Recipe> recipes = recipeRepository.findAll(spec, pageable);
    return recipes.map(this::mapToResponseDTO);
}
```

## Important Notes

- **JPA ModelGen**: Metamodel classes (`Recipe_`, `Entity_`) are generated at compile time by Hibernate JPA ModelGen annotation processor
- **Case Insensitive**: String filtering is case-insensitive (converts to lowercase)
- **AND Logic**: Multiple filters are combined with AND logic
- **Null Safety**: Inactive filters (null or empty values) are ignored in the specification
- **Default Operator**: Filters have default operators if not specified (StringFilter defaults to STARTS_WITH, NumericFilter to GREATER_THAN)
