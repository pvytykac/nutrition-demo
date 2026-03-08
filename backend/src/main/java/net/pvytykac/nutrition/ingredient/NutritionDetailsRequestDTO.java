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

    @NotNull
    @PositiveOrZero
    private BigDecimal fatContent;

    @NotNull
    @PositiveOrZero
    private BigDecimal carbsContent;

    @NotNull
    @PositiveOrZero
    private BigDecimal proteinContent;

    @NotNull
    @PositiveOrZero
    private BigDecimal phenylalanineContent;

    @PositiveOrZero
    private BigDecimal kilocalories;
}
