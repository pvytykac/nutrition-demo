package net.pvytykac.nutrition.recipe;

import net.pvytykac.nutrition.RepositoryTestBase;
import net.pvytykac.nutrition.ingredient.Ingredient;
import net.pvytykac.nutrition.ingredient.NutritionDetails;
import net.pvytykac.nutrition.ingredient.Unit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RecipeRepository")
class RecipeRepositoryTest extends RepositoryTestBase {

    @Autowired
    private RecipeRepository recipeRepository;

    private Ingredient createTestIngredient(String name) {
        return testEntityManager.persistFlushFind(Ingredient.builder()
                .name(name)
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(NutritionDetails.builder()
                        .fatContent(new BigDecimal("1.0"))
                        .carbsContent(new BigDecimal("10.0"))
                        .proteinContent(new BigDecimal("5.0"))
                        .phenylalanineContent(new BigDecimal("50.0"))
                        .kilocalories(new BigDecimal("100.0"))
                        .build())
                .build());
    }

    @Test
    @DisplayName("should save and retrieve recipe with ingredients")
    void shouldSaveAndRetrieveRecipe() {
        // given
        Ingredient ingredient = createTestIngredient("Potato");
        
        Recipe recipe = Recipe.builder()
                .name("Baked Potatoes")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();

        RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("2.5"))
                .build();
        recipe.getIngredients().add(recipeIngredient);

        // when
        Recipe saved = recipeRepository.save(recipe);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Baked Potatoes");
        assertThat(saved.getUserId()).isEqualTo("userA");
        assertThat(saved.getIngredients()).hasSize(1);
        assertThat(saved.getIngredients().get(0).getMultiplier()).isEqualByComparingTo(new BigDecimal("2.5"));
        assertThat(saved.getIngredients().get(0).getIngredient().getName()).isEqualTo("Potato");
    }

    @Test
    @DisplayName("should retrieve all recipes for a user")
    void shouldRetrieveAllRecipesForUser() {
        // given
        Ingredient ingredient = createTestIngredient("Potato");
        
        Recipe recipe1 = Recipe.builder()
                .name("Recipe 1")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();
        recipe1.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe1)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("1.0"))
                .build());

        Recipe recipe2 = Recipe.builder()
                .name("Recipe 2")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();
        recipe2.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe2)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("2.0"))
                .build());

        Recipe recipe3 = Recipe.builder()
                .name("Recipe 3")
                .userId("userB")
                .ingredients(new ArrayList<>())
                .build();
        recipe3.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe3)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("3.0"))
                .build());

        testEntityManager.persistFlushFind(recipe1);
        testEntityManager.persistFlushFind(recipe2);
        testEntityManager.persistFlushFind(recipe3);

        Pageable pageable = PageRequest.of(0, 20);

        // when
        var result = recipeRepository.findAllByUserId("userA", pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().stream().map(Recipe::getName))
                .containsExactlyInAnyOrder("Recipe 1", "Recipe 2");
    }

    @Test
    @DisplayName("should find recipe by id and user id")
    void shouldFindRecipeByIdAndUserId() {
        // given
        Ingredient ingredient = createTestIngredient("Potato");
        
        Recipe recipe = Recipe.builder()
                .name("Test Recipe")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();
        recipe.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("1.0"))
                .build());

        Recipe saved = testEntityManager.persistFlushFind(recipe);

        // when
        Optional<Recipe> result = recipeRepository.findByIdAndUserId(saved.getId(), "userA");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Recipe");
    }

    @Test
    @DisplayName("should return empty when recipe belongs to different user")
    void shouldReturnEmptyWhenRecipeBelongsToDifferentUser() {
        // given
        Ingredient ingredient = createTestIngredient("Potato");
        
        Recipe recipe = Recipe.builder()
                .name("Test Recipe")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();
        recipe.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("1.0"))
                .build());

        Recipe saved = testEntityManager.persistFlushFind(recipe);

        // when
        Optional<Recipe> result = recipeRepository.findByIdAndUserId(saved.getId(), "userB");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should return empty when recipe not found")
    void shouldReturnEmptyWhenRecipeNotFound() {
        // given
        UUID nonExistentId = UUID.fromString("99999999-9999-9999-9999-999999999999");

        // when
        Optional<Recipe> result = recipeRepository.findByIdAndUserId(nonExistentId, "userA");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should check existence by id and user id")
    void shouldCheckExistenceByIdAndUserId() {
        // given
        Ingredient ingredient = createTestIngredient("Potato");
        
        Recipe recipe = Recipe.builder()
                .name("Test Recipe")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();
        recipe.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("1.0"))
                .build());

        Recipe saved = testEntityManager.persistFlushFind(recipe);

        // when/then
        assertThat(recipeRepository.existsByIdAndUserId(saved.getId(), "userA")).isTrue();
        assertThat(recipeRepository.existsByIdAndUserId(saved.getId(), "userB")).isFalse();
    }

    @Test
    @DisplayName("should delete recipe and cascade delete ingredients")
    void shouldDeleteRecipeAndCascadeDeleteIngredients() {
        // given
        Ingredient ingredient = createTestIngredient("Potato");
        
        Recipe recipe = Recipe.builder()
                .name("Test Recipe")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();
        recipe.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("1.0"))
                .build());

        Recipe saved = testEntityManager.persistFlushFind(recipe);
        UUID recipeId = saved.getId();

        // when
        recipeRepository.delete(saved);
        testEntityManager.flush();
        testEntityManager.clear();

        // then
        assertThat(recipeRepository.findById(recipeId)).isEmpty();
    }

    @Test
    @DisplayName("should update recipe name")
    void shouldUpdateRecipeName() {
        // given
        Ingredient ingredient = createTestIngredient("Potato");
        
        Recipe recipe = Recipe.builder()
                .name("Original Name")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();
        recipe.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(ingredient)
                .multiplier(new BigDecimal("1.0"))
                .build());

        Recipe saved = testEntityManager.persistFlushFind(recipe);
        saved.setName("Updated Name");

        // when
        Recipe updated = recipeRepository.save(saved);

        // then
        assertThat(updated.getName()).isEqualTo("Updated Name");
    }

    @Test
    @DisplayName("should replace ingredients on update")
    void shouldReplaceIngredientsOnUpdate() {
        // given
        Ingredient potato = createTestIngredient("Potato");
        Ingredient butter = createTestIngredient("Butter");
        
        Recipe recipe = Recipe.builder()
                .name("Test Recipe")
                .userId("userA")
                .ingredients(new ArrayList<>())
                .build();
        recipe.getIngredients().add(RecipeIngredient.builder()
                .recipe(recipe)
                .ingredient(potato)
                .multiplier(new BigDecimal("1.0"))
                .build());

        Recipe saved = testEntityManager.persistFlushFind(recipe);
        
        // Replace ingredients
        saved.getIngredients().clear();
        saved.getIngredients().add(RecipeIngredient.builder()
                .recipe(saved)
                .ingredient(butter)
                .multiplier(new BigDecimal("0.5"))
                .build());

        // when
        Recipe updated = recipeRepository.save(saved);
        testEntityManager.flush();
        testEntityManager.clear();

        Recipe retrieved = recipeRepository.findById(updated.getId()).orElseThrow();

        // then
        assertThat(retrieved.getIngredients()).hasSize(1);
        assertThat(retrieved.getIngredients().get(0).getIngredient().getName()).isEqualTo("Butter");
        assertThat(retrieved.getIngredients().get(0).getMultiplier()).isEqualByComparingTo(new BigDecimal("0.5"));
    }
}
