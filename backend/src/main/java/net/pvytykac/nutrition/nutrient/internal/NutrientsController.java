package net.pvytykac.nutrition.nutrient.internal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/nutrients")
class NutrientsController {

    @GetMapping
    public Nutrient getNutrient() {
        return new Nutrient("1", "protein");
    }

    public record Nutrient(String id, String name) {
    }

}
