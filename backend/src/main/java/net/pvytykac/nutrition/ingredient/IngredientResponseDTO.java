package net.pvytykac.nutrition.ingredient;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientResponseDTO {

    private UUID id;
    private String name;
    private NutritionDetailsResponseDTO nutritionDetails;
}
