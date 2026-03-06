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

    @Column(name = "fat_per_unit")
    private BigDecimal fat;

    @Column(name = "carbs_per_unit")
    private BigDecimal carbs;

    @Column(name = "protein_per_unit")
    private BigDecimal protein;

    @Column(name = "phenylalanine_per_unit")
    private BigDecimal phenylalanine;

    @Column(name = "unit")
    private String unit;
}
