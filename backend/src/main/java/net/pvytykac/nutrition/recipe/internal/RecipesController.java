package net.pvytykac.nutrition.recipe.internal;

import net.pvytykac.nutrition.common.security.HasUserOrAdminRole;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/recipes")
@HasUserOrAdminRole
class RecipesController {

    @GetMapping
    public Recipe getNutrient() {
        return new Recipe("1", "mashed potatoes");
    }

    public record Recipe(String id, String name) {
    }

}
