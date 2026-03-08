package net.pvytykac.nutrition.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.pvytykac.nutrition.ingredient.Unit;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload for a recipe ingredient with calculated values")
public class RecipeIngredientResponseDTO {

    @Schema(description = "Unique identifier of the recipe ingredient", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID id;

    @Schema(description = "ID of the referenced ingredient", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID ingredientId;

    @Schema(description = "Name of the ingredient", example = "Potato")
    private String ingredientName;

    @Schema(description = "Base quantity of the ingredient", example = "100.0000")
    private BigDecimal baseQuantity;

    @Schema(description = "Unit of measurement", example = "GRAM")
    private Unit unit;

    @Schema(description = "Multiplier applied to the base quantity", example = "2.5")
    private BigDecimal multiplier;

    @Schema(description = "Calculated quantity (base quantity × multiplier)", example = "250.0000")
    private BigDecimal calculatedQuantity;

    @Schema(description = "Calculated fat content")
    private BigDecimal fatContent;

    @Schema(description = "Calculated carbs content")
    private BigDecimal carbsContent;

    @Schema(description = "Calculated protein content")
    private BigDecimal proteinContent;

    @Schema(description = "Calculated phenylalanine content")
    private BigDecimal phenylalanineContent;

    @Schema(description = "Calculated kilocalories")
    private BigDecimal kilocalories;

}
