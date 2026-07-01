package net.pvytykac.nutrition.ingredient.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/ingredients")
class IngredientsController {

    @GetMapping
    Page<Ingredient> getIngredients() {
        return new PageImpl<>(List.of(new Ingredient("1", "potato")));
    }

    public record Ingredient(String id, String name) {
    }

}
