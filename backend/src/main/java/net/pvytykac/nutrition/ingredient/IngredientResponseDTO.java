package net.pvytykac.nutrition.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "Unique identifier of the ingredient")
    private UUID id;

    @Schema(description = "Name of the ingredient")
    private String name;

    @Schema(description = "Quantity of the ingredient")
    private BigDecimal quantity;

    @Schema(description = "Unit of measurement")
    private Unit unit;

    @Schema(description = "Nutritional information")
    private NutritionDetailsResponseDTO nutritionDetails;
}
