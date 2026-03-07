package net.pvytykac.nutrition.ingredient;

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

    private BigDecimal fatContent;
    private BigDecimal carbsContent;
    private BigDecimal proteinContent;
    private BigDecimal phenylalanineContent;
    private BigDecimal kilocalories;
}
