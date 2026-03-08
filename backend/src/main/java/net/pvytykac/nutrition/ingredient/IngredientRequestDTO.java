package net.pvytykac.nutrition.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientRequestDTO {

    @NotBlank
    @Schema(description = "Name of the ingredient")
    private String name;

    @NotNull
    @Positive
    @Schema(description = "Quantity of the ingredient", example = "100")
    private BigDecimal quantity;

    @NotNull
    @Schema(description = "Unit of measurement")
    private Unit unit;

    @NotNull
    @Valid
    @Schema(description = "Nutritional information")
    private NutritionDetailsRequestDTO nutritionDetails;
}
