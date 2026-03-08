package net.pvytykac.nutrition.ingredient;

import lombok.Data;
import net.pvytykac.nutrition.util.filtering.NumericFilter;
import net.pvytykac.nutrition.util.filtering.StringFilter;

@Data
public class IngredientsQueryParameters {
    StringFilter name;
    NumericFilter fatContent;
    NumericFilter proteinContent;
    NumericFilter carbsContent;
    NumericFilter phenylalanineContent;
}
