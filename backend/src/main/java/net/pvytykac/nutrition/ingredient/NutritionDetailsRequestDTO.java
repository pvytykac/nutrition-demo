package net.pvytykac.nutrition.ingredient;

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

    @NotNull(message = "Fat content is required")
    @PositiveOrZero(message = "Fat content must be zero or positive")
    private BigDecimal fatContent;

    @NotNull(message = "Carbs content is required")
    @PositiveOrZero(message = "Carbs content must be zero or positive")
    private BigDecimal carbsContent;

    @NotNull(message = "Protein content is required")
    @PositiveOrZero(message = "Protein content must be zero or positive")
    private BigDecimal proteinContent;

    @NotNull(message = "Phenylalanine content is required")
    @PositiveOrZero(message = "Phenylalanine content must be zero or positive")
    private BigDecimal phenylalanineContent;

    @PositiveOrZero(message = "Kilocalories must be zero or positive")
    private BigDecimal kilocalories;
}
