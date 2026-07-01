package net.pvytykac.nutrition.nutrient.internal;

import net.pvytykac.nutrition.common.exceptions.ApplicationException;

public class DuplicateNutrientNameException extends ApplicationException {

    public DuplicateNutrientNameException(String name) {
        super("A nutrient with name '" + name + "' already exists");
    }
}
