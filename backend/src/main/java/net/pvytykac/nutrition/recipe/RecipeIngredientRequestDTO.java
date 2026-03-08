package net.pvytykac.nutrition.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for a recipe ingredient")
public class RecipeIngredientRequestDTO {

    @NotNull(message = "Ingredient ID is required")
    @Schema(description = "ID of the ingredient", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID ingredientId;

    @NotNull(message = "Multiplier is required")
    @Positive(message = "Multiplier must be positive")
    @Schema(description = "Multiplier for the ingredient quantity", example = "2.5")
    private BigDecimal multiplier;

}
