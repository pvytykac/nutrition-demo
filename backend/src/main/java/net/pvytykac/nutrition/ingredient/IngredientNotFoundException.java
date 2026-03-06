package net.pvytykac.nutrition.ingredient;

import lombok.Getter;

@Getter
public class IngredientNotFoundException extends RuntimeException {

    public IngredientNotFoundException(Long id) {
        this.id = id;
    }

    private final Long id;

    @Override
    public String getMessage() {
        return "Ingredient not found with id: " + id;
    }
}
