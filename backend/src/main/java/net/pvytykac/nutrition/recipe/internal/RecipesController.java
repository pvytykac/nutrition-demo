package net.pvytykac.nutrition.recipe.internal;

import net.pvytykac.nutrition.common.security.HasUserOrAdminRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/recipes")
@HasUserOrAdminRole
class RecipesController {

    @GetMapping
    Page<Recipe> getRecipes() {
        return new PageImpl<>(List.of(new Recipe("1", "mashed potatoes")));
    }

    public record Recipe(String id, String name) {
    }

}
