package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.util.filtering.FilterBuilder;
import net.pvytykac.nutrition.util.filtering.NumberOperator;
import net.pvytykac.nutrition.util.filtering.StringOperator;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

/**
 * Filter specifications for Ingredient entity.
 */
public final class IngredientFilter {

    private IngredientFilter() {}

    /**
     * Creates a specification that filters by the name field.
     */
    public static Specification<Ingredient> nameContains(String value, StringOperator operator) {
        return FilterBuilder.stringFilter(
                root -> root.get("name"),
                value,
                operator);
    }

    /**
     * Creates a specification that filters by the phenylalanine field in nutrition details.
     */
    public static Specification<Ingredient> phenylalanineFilter(
            BigDecimal value, BigDecimal secondValue, NumberOperator operator) {
        
        return FilterBuilder.comparableFilter(
                root -> root.get("nutritionDetails").get("phenylalanineContent"),
                value,
                secondValue,
                operator);
    }

    /**
     * Combines multiple specifications using AND logic.
     */
    public static Specification<Ingredient> combine(List<Specification<Ingredient>> specs) {
        return FilterBuilder.combine(specs);
    }
}
