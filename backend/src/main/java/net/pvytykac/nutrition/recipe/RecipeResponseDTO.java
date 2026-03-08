package net.pvytykac.nutrition.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.pvytykac.nutrition.ingredient.Unit;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Response payload for a recipe")
public class RecipeResponseDTO {

    @Schema(description = "Unique identifier of the recipe", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID id;

    @Schema(description = "Name of the recipe", example = "Baked Potatoes")
    private String name;

    @Schema(description = "User ID who owns the recipe", example = "userA")
    private String userId;

    @Schema(description = "List of ingredients with calculated quantities")
    private List<RecipeIngredientResponseDTO> ingredients;

}
