package net.pvytykac.nutrition.nutrient.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestionRequestDTO {

    @NotBlank
    private String name;

    private BigDecimal kcalPerGram;

    @NotNull
    private NutrientUnit defaultUnit;

}
