package net.pvytykac.nutrition.ingredient;

import jakarta.persistence.criteria.Root;
import lombok.Builder;
import lombok.Getter;
import net.pvytykac.nutrition.util.filtering.NumericFilter;
import net.pvytykac.nutrition.util.filtering.SpecificationBuilder;
import net.pvytykac.nutrition.util.filtering.StringFilter;
import org.springframework.data.jpa.domain.Specification;

/**
 * Container class for ingredient filtering criteria.
 * Knows which filters apply to the Ingredient entity and provides
 * field expressions for the SpecificationBuilder to build specifications.
 */
@Getter
@Builder
public class IngredientFilter {

    private final StringFilter nameFilter;
    private final NumericFilter fatContentFilter;
    private final NumericFilter proteinContentFilter;
    private final NumericFilter carbsContentFilter;
    private final NumericFilter phenylalanineContentFilter;

    /**
     * Converts this filter container to a combined JPA Specification.
     * Delegates to SpecificationBuilder with entity-specific field expressions.
     *
     * @return Specification that combines all active filters with AND logic,
     *         or null if no filters are active
     */
    public Specification<Ingredient> toSpecification() {
        return SpecificationBuilder.combine(
                nameSpecification(),
                fatContentSpecification(),
                proteinContentSpecification(),
                carbsContentSpecification(),
                phenylalanineContentSpecification()
        );
    }

    private Specification<Ingredient> nameSpecification() {
        if (!isNameFilterActive()) {
            return null;
        }
        return SpecificationBuilder.stringFilter(
                nameFilter,
                (Root<Ingredient> root) -> root.get(Ingredient_.NAME)
        );
    }

    private Specification<Ingredient> fatContentSpecification() {
        if (!isFatContentFilterActive()) {
            return null;
        }
        return SpecificationBuilder.numericFilter(
                fatContentFilter,
                (Root<Ingredient> root) -> root.get(Ingredient_.NUTRITION_DETAILS).get(NutritionDetails_.FAT_CONTENT)
        );
    }

    private Specification<Ingredient> proteinContentSpecification() {
        if (!isProteinContentFilterActive()) {
            return null;
        }
        return SpecificationBuilder.numericFilter(
                proteinContentFilter,
                (Root<Ingredient> root) -> root.get(Ingredient_.NUTRITION_DETAILS).get(NutritionDetails_.PROTEIN_CONTENT)
        );
    }

    private Specification<Ingredient> carbsContentSpecification() {
        if (!isCarbsContentFilterActive()) {
            return null;
        }
        return SpecificationBuilder.numericFilter(
                carbsContentFilter,
                (Root<Ingredient> root) -> root.get(Ingredient_.NUTRITION_DETAILS).get(NutritionDetails_.CARBS_CONTENT)
        );
    }

    private Specification<Ingredient> phenylalanineContentSpecification() {
        if (!isPhenylalanineContentFilterActive()) {
            return null;
        }
        return SpecificationBuilder.numericFilter(
                phenylalanineContentFilter,
                (Root<Ingredient> root) -> root.get(Ingredient_.NUTRITION_DETAILS).get(NutritionDetails_.PHENYLALANINE_CONTENT)
        );
    }

    public boolean isNameFilterActive() {
        return nameFilter != null && nameFilter.isActive();
    }

    public boolean isFatContentFilterActive() {
        return fatContentFilter != null && fatContentFilter.isActive();
    }

    public boolean isProteinContentFilterActive() {
        return proteinContentFilter != null && proteinContentFilter.isActive();
    }

    public boolean isCarbsContentFilterActive() {
        return carbsContentFilter != null && carbsContentFilter.isActive();
    }

    public boolean isPhenylalanineContentFilterActive() {
        return phenylalanineContentFilter != null && phenylalanineContentFilter.isActive();
    }
}
