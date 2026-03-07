package net.pvytykac.nutrition.ingredient;

import java.math.BigDecimal;
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
    private BigDecimal quantity;
    private Unit unit;
    private NutritionDetailsResponseDTO nutritionDetails;
}
