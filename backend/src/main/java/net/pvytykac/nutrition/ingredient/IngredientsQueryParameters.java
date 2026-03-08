package net.pvytykac.nutrition.ingredient;

import lombok.Data;
import net.pvytykac.nutrition.util.filtering.EnumFilter;
import net.pvytykac.nutrition.util.filtering.NumericFilter;
import net.pvytykac.nutrition.util.filtering.StringFilter;

@Data
public class IngredientsQueryParameters {
    StringFilter name;
    UnitFilter unit;
    NumericFilter fatContent;
    NumericFilter proteinContent;
    NumericFilter carbsContent;
    NumericFilter phenylalanineContent;

    public static class UnitFilter extends EnumFilter<Unit> {
    }
}
