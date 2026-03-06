package net.pvytykac.nutrition.ingredient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
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
public class NutritionDetailsRequestDTO {

    @NotNull(message = "Fat amount is required")
    @PositiveOrZero(message = "Fat amount must be zero or positive")
    private BigDecimal fat;

    @NotNull(message = "Carbs amount is required")
    @PositiveOrZero(message = "Carbs amount must be zero or positive")
    private BigDecimal carbs;

    @NotNull(message = "Protein amount is required")
    @PositiveOrZero(message = "Protein amount must be zero or positive")
    private BigDecimal protein;

    @NotNull(message = "Phenylalanine amount is required")
    @PositiveOrZero(message = "Phenylalanine amount must be zero or positive")
    private BigDecimal phenylalanine;

    @NotBlank(message = "Unit is required")
    private String unit;
}
