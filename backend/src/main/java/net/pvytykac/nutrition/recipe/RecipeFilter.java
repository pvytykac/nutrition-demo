package net.pvytykac.nutrition.recipe;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.pvytykac.nutrition.common.filtering.SpecificationBuilder;
import net.pvytykac.nutrition.common.filtering.StringFilter;
import org.springframework.data.jpa.domain.Specification;

@Getter
@Setter
@Builder
class RecipeFilter {

    private StringFilter name;

    public boolean isActive() {
        return (name != null && name.isActive());
    }

    public Specification<Recipe> toSpecification() {
        if (!isActive()) {
            return null;
        }

        Specification<Recipe> nameSpec = SpecificationBuilder.stringFilter(
                name,
                root -> root.get(Recipe_.NAME)
        );

        return SpecificationBuilder.combine(nameSpec);
    }

}
