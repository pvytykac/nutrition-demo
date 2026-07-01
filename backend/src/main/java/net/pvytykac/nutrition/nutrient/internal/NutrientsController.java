package net.pvytykac.nutrition.nutrient.internal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/nutrients")
class NutrientsController {

    @GetMapping
    Page<Nutrient> getNutrients() {
        return new PageImpl<>(List.of(new Nutrient("1", "protein")));
    }

    public record Nutrient(String id, String name) {
    }

}
