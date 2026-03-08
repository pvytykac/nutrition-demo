package net.pvytykac.nutrition.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Request payload for creating or updating a recipe")
public class RecipeRequestDTO {

    @NotBlank(message = "Recipe name is required")
    @Size(max = 255, message = "Recipe name must not exceed 255 characters")
    @Schema(description = "Name of the recipe", example = "Baked Potatoes")
    private String name;

    @NotEmpty(message = "At least one ingredient is required")
    @Valid
    @Schema(description = "List of ingredients with their multipliers")
    private List<RecipeIngredientRequestDTO> ingredients;

}
