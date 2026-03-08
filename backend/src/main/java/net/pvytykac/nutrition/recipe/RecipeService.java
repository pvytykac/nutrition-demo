package net.pvytykac.nutrition.recipe;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import net.pvytykac.nutrition.ingredient.Ingredient;
import net.pvytykac.nutrition.ingredient.IngredientRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class RecipeService {

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    public RecipeResponseDTO createRecipe(String userId, RecipeRequestDTO request) {
        log.info("Creating new recipe '{}' for user: {}", request.getName(), userId);

        Recipe recipe = Recipe.builder()
                .name(request.getName())
                .userId(userId)
                .build();

        List<RecipeIngredient> recipeIngredients = request.getIngredients().stream()
                .map(ri -> createRecipeIngredient(recipe, ri))
                .toList();

        recipe.setIngredients(recipeIngredients);

        Recipe saved = recipeRepository.save(recipe);
        log.info("Created recipe with id: {} for user: {}", saved.getId(), userId);

        return mapToResponseDTO(saved);
    }

    public RecipeResponseDTO getRecipeById(String userId, UUID id) {
        log.debug("Fetching recipe with id: {} for user: {}", id, userId);

        Recipe recipe = recipeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", id));

        return mapToResponseDTO(recipe);
    }

    public Page<RecipeResponseDTO> searchRecipes(String userId, RecipeFilter filter, Pageable pageable) {
        log.debug("Searching recipes for user: {} with filters", userId);

        Specification<Recipe> spec = filter.toSpecification();

        Page<Recipe> recipes;
        if (spec != null) {
            recipes = recipeRepository.findAllByUserId(userId, spec, pageable);
        } else {
            recipes = recipeRepository.findAllByUserId(userId, pageable);
        }

        log.debug("Found {} recipes for user: {}", recipes.getTotalElements(), userId);

        return recipes.map(this::mapToResponseDTO);
    }

    public RecipeResponseDTO updateRecipe(String userId, UUID id, RecipeRequestDTO request) {
        log.info("Updating recipe with id: {} for user: {}", id, userId);

        Recipe recipe = recipeRepository.findByIdAndUserIdForUpdate(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", id));

        recipe.setName(request.getName());

        recipe.getIngredients().clear();

        List<RecipeIngredient> newIngredients = request.getIngredients().stream()
                .map(ri -> createRecipeIngredient(recipe, ri))
                .toList();

        recipe.getIngredients().addAll(newIngredients);

        Recipe saved = recipeRepository.save(recipe);
        log.info("Updated recipe with id: {} for user: {}", saved.getId(), userId);

        return mapToResponseDTO(saved);
    }

    public void deleteRecipe(String userId, UUID id) {
        log.info("Deleting recipe with id: {} for user: {}", id, userId);

        Recipe recipe = recipeRepository.findByIdAndUserIdForUpdate(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Recipe", id));

        recipeRepository.delete(recipe);
        log.info("Deleted recipe with id: {} for user: {}", id, userId);
    }

    private RecipeIngredient createRecipeIngredient(Recipe recipe, RecipeIngredientRequestDTO dto) {
        Ingredient ingredient = ingredientRepository.findById(dto.getIngredientId())
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", dto.getIngredientId()));

        return RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .multiplier(dto.getMultiplier())
                .build();
    }

    private RecipeIngredientResponseDTO mapToRecipeIngredientResponse(RecipeIngredient ri) {
        Ingredient ingredient = ri.getIngredient();
        BigDecimal multiplier = ri.getMultiplier();

        return RecipeIngredientResponseDTO.builder()
                .id(ri.getId())
                .ingredientId(ingredient.getId())
                .ingredientName(ingredient.getName())
                .baseQuantity(ingredient.getQuantity())
                .unit(ingredient.getUnit())
                .multiplier(multiplier)
                .calculatedQuantity(ingredient.getQuantity().multiply(multiplier))
                .fatContent(multiplyNullable(ingredient.getNutritionDetails().getFatContent(), multiplier))
                .carbsContent(multiplyNullable(ingredient.getNutritionDetails().getCarbsContent(), multiplier))
                .proteinContent(multiplyNullable(ingredient.getNutritionDetails().getProteinContent(), multiplier))
                .phenylalanineContent(multiplyNullable(ingredient.getNutritionDetails().getPhenylalanineContent(), multiplier))
                .kilocalories(multiplyNullable(ingredient.getNutritionDetails().getKilocalories(), multiplier))
                .build();
    }

    private BigDecimal multiplyNullable(BigDecimal value, BigDecimal multiplier) {
        if (value == null) {
            return null;
        }
        return value.multiply(multiplier);
    }

    private RecipeResponseDTO mapToResponseDTO(Recipe recipe) {
        return RecipeResponseDTO.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .userId(recipe.getUserId())
                .ingredients(recipe.getIngredients().stream()
                        .map(this::mapToRecipeIngredientResponse)
                        .toList())
                .build();
    }

}
