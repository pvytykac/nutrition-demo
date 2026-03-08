package net.pvytykac.nutrition.recipe;

import lombok.Data;
import net.pvytykac.nutrition.common.filtering.StringFilter;

@Data
public class RecipesQueryParameters {

    StringFilter name;

}
