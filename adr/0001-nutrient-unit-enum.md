# ADR-0001: Nutrient Unit Enum

## Status

Accepted

## Date

2026-07-01

## Context

Nutrients need a unit of measure. Macronutrients (carbohydrates, protein, fat) are conventionally measured in grams, while trace amino acids such as phenylalanine are measured in milligrams. The data model must support both without ambiguity.

The unit affects how ingredients record nutritional values and how calculations (e.g., total kcal, phenylalanine exchanges) are performed. Future nutrients like vitamins, minerals, or amino acids may have their own unit conventions.

## Decision

Introduce a `NutrientUnit` enum with two values: `GRAM` and `MILLIGRAM`. Every `Nutrient` entity carries a `defaultUnit` field. The unit is stored as a VARCHAR column in the database.

When the ingredient module records quantities, it will reference the nutrient's `defaultUnit` to determine the expected measurement unit.

## Consequences

- Good: Explicit, typed representation of the unit eliminates ambiguity in data entry and calculations.
- Good: Adding new units (e.g., MICROGRAM, MILLILITER) is a one-enum-value change.
- Bad: Two-unit system adds a small cognitive overhead — data entry must know which unit applies to each nutrient.
- Bad: Quantity conversions between units (mg ↔ g) must be handled in the domain layer, not the database.
