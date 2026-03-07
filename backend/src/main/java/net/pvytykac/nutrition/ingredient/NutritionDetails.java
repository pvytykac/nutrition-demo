package net.pvytykac.nutrition.ingredient;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NutritionDetails {

    @Column(name = "fat_content", precision = 10, scale = 4)
    private BigDecimal fatContent;

    @Column(name = "carbs_content", precision = 10, scale = 4)
    private BigDecimal carbsContent;

    @Column(name = "protein_content", precision = 10, scale = 4)
    private BigDecimal proteinContent;

    @Column(name = "phenylalanine_content", precision = 10, scale = 4)
    private BigDecimal phenylalanineContent;

    @Column(name = "kilocalories", precision = 10, scale = 4)
    private BigDecimal kilocalories;
}
