package net.pvytykac.nutrition.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(description = "Fat content in grams", example = "5.0")
    private BigDecimal fatContent;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Carbohydrate content in grams", example = "20.0")
    private BigDecimal carbsContent;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Protein content in grams", example = "3.0")
    private BigDecimal proteinContent;

    @NotNull
    @PositiveOrZero
    @Schema(description = "Phenylalanine content in milligrams (important for PKU)", example = "50.0")
    private BigDecimal phenylalanineContent;

    @PositiveOrZero
    @Schema(description = "Energy in kilocalories", example = "150.0")
    private BigDecimal kilocalories;
}
