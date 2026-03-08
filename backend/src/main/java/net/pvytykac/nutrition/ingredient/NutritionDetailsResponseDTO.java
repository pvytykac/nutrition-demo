package net.pvytykac.nutrition.ingredient;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class NutritionDetailsResponseDTO {

    @Schema(description = "Fat content in grams")
    private BigDecimal fatContent;

    @Schema(description = "Carbohydrate content in grams")
    private BigDecimal carbsContent;

    @Schema(description = "Protein content in grams")
    private BigDecimal proteinContent;

    @Schema(description = "Phenylalanine content in milligrams (important for PKU)")
    private BigDecimal phenylalanineContent;

    @Schema(description = "Energy in kilocalories")
    private BigDecimal kilocalories;
}
