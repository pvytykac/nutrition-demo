package net.pvytykac.nutrition.ingredient.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/ingredients")
class IngredientsController {

    @GetMapping
    Ingredient getNutrient() {
        return new Ingredient("1", "potato");
    }

    public record Ingredient(String id, String name) {
    }

}
