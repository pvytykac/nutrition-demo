package net.pvytykac.nutrition.ingredient;

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
    private String name;

    @NotNull
    @Positive
    private BigDecimal quantity;

    @NotNull
    private Unit unit;

    @NotNull
    @Valid
    private NutritionDetailsRequestDTO nutritionDetails;
}
