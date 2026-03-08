# Ingredient Module

This module manages food ingredients and their nutritional information.

## Purpose

Provides CRUD operations for ingredients including their nutritional details:
- Fats, carbs, protein content
- Phenylalanine (for PKU tracking)
- Kilocalories
- Measurement units (grams, milliliters)

## API Endpoints

All endpoints require **admin** role.

| Method | Path | Description |
|--------|------|-------------|
| POST | `/v1/ingredients` | Create new ingredient |
| GET | `/v1/ingredients/{id}` | Get ingredient by ID |
| GET | `/v1/ingredients` | List ingredients (paginated, filterable) |
| PUT | `/v1/ingredients/{id}` | Update ingredient |
| DELETE | `/v1/ingredients/{id}` | Delete ingredient |

## Filtering

The list endpoint supports query parameter filtering:

```
GET /v1/ingredients?name.value=Chicken&name.operator=CONTAINS
GET /v1/ingredients?fatContent.value=10&fatContent.operator=GREATER_THAN
GET /v1/ingredients?phenylalanineContent.value=5&phenylalanineContent.operator=LOWER_THAN
```

See [FILTERING.md](../../../FILTERING.md) for detailed filtering documentation.

## Data Model

### Ingredient Entity

```java
@Entity
@Table(name = "ingredients")
public class Ingredient {
    UUID id;
    String name;
    BigDecimal quantity;
    Unit unit;              // GRAM, MILLILITER
    NutritionDetails nutritionDetails;
}
```

### NutritionDetails (Embedded)

```java
@Embeddable
public class NutritionDetails {
    BigDecimal fatContent;
    BigDecimal carbsContent;
    BigDecimal proteinContent;
    BigDecimal phenylalanineContent;
    BigDecimal kilocalories;
}
```

## Module Structure

This package follows the flat structure pattern:

| File | Type | Visibility | Purpose |
|------|------|------------|---------|
| `Ingredient.java` | Entity | package-private | JPA entity |
| `IngredientRepository.java` | Repository | package-private | Data access |
| `IngredientService.java` | Service | package-private | Business logic |
| `IngredientController.java` | Controller | public | HTTP API |
| `IngredientRequestDTO.java` | DTO | public | POST/PUT payload |
| `IngredientResponseDTO.java` | DTO | public | Response payload |
| `IngredientFilter.java` | Filter | public | Query filtering |
| `IngredientsQueryParameters.java` | Parameters | package-private | Query binding |
| `Unit.java` | Enum | public | Measurement units |

## Creating a New Module

Use this module as a template when creating new modules:

1. Copy the structure (entity, repo, service, controller, DTOs)
2. Replace `Ingredient` with your entity name
3. Adjust fields and validation rules
4. Add filtering if needed
5. Write corresponding tests

See [ARCHITECTURE.md](../../../ARCHITECTURE.md) for detailed module creation guide.
