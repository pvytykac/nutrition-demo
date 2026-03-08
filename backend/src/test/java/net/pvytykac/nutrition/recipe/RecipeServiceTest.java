package net.pvytykac.nutrition.recipe;

import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import net.pvytykac.nutrition.ingredient.Ingredient;
import net.pvytykac.nutrition.ingredient.IngredientRepository;
import net.pvytykac.nutrition.ingredient.NutritionDetails;
import net.pvytykac.nutrition.ingredient.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("RecipeService")
@SuppressWarnings("unchecked")
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private RecipeService recipeService;

    private static final UUID TEST_RECIPE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID TEST_INGREDIENT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final String TEST_USER_ID = "userA";
    private static final String OTHER_USER_ID = "userB";

    private Recipe createTestRecipe() {
        return Recipe.builder()
                .id(TEST_RECIPE_ID)
                .name("Baked Potatoes")
                .userId(TEST_USER_ID)
                .ingredients(new ArrayList<>())
                .build();
    }

    private Ingredient createTestIngredient() {
        return Ingredient.builder()
                .id(TEST_INGREDIENT_ID)
                .name("Potato")
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(NutritionDetails.builder()
                        .fatContent(new BigDecimal("0.1"))
                        .carbsContent(new BigDecimal("17.0"))
                        .proteinContent(new BigDecimal("2.0"))
                        .phenylalanineContent(new BigDecimal("50.0"))
                        .kilocalories(new BigDecimal("77.0"))
                        .build())
                .build();
    }

    @Nested
    @DisplayName("createRecipe")
    class CreateRecipe {

        @Test
        @DisplayName("should create recipe successfully with ingredients")
        void shouldCreateRecipeSuccessfully() {
            // given
            RecipeRequestDTO request = RecipeRequestDTO.builder()
                    .name("Baked Potatoes")
                    .ingredients(List.of(
                            RecipeIngredientRequestDTO.builder()
                                    .ingredientId(TEST_INGREDIENT_ID)
                                    .multiplier(new BigDecimal("2.5"))
                                    .build()
                    ))
                    .build();

            Ingredient ingredient = createTestIngredient();
            when(ingredientRepository.findById(TEST_INGREDIENT_ID)).thenReturn(Optional.of(ingredient));

            Recipe savedRecipe = createTestRecipe();
            savedRecipe.setIngredients(List.of(
                    RecipeIngredient.builder()
                            .id(UUID.randomUUID())
                            .recipe(savedRecipe)
                            .ingredient(ingredient)
                            .multiplier(new BigDecimal("2.5"))
                            .build()
            ));
            when(recipeRepository.save(any(Recipe.class))).thenReturn(savedRecipe);

            // when
            RecipeResponseDTO result = recipeService.createRecipe(TEST_USER_ID, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Baked Potatoes");
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
            assertThat(result.getIngredients()).hasSize(1);
            assertThat(result.getIngredients().getFirst().getMultiplier()).isEqualTo(new BigDecimal("2.5"));
            assertThat(result.getIngredients().getFirst().getCalculatedQuantity()).isEqualByComparingTo(new BigDecimal("250.0"));
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when ingredient does not exist")
        void shouldThrowExceptionWhenIngredientNotFound() {
            // given
            RecipeRequestDTO request = RecipeRequestDTO.builder()
                    .name("Baked Potatoes")
                    .ingredients(List.of(
                            RecipeIngredientRequestDTO.builder()
                                    .ingredientId(TEST_INGREDIENT_ID)
                                    .multiplier(new BigDecimal("2.5"))
                                    .build()
                    ))
                    .build();

            when(ingredientRepository.findById(TEST_INGREDIENT_ID)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> recipeService.createRecipe(TEST_USER_ID, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ingredient");
        }
    }

    @Nested
    @DisplayName("getRecipeById")
    class GetRecipeById {

        @Test
        @DisplayName("should return recipe when found and owned by user")
        void shouldReturnRecipeWhenFoundAndOwned() {
            // given
            Recipe recipe = createTestRecipe();
            when(recipeRepository.findByIdAndUserId(TEST_RECIPE_ID, TEST_USER_ID))
                    .thenReturn(Optional.of(recipe));

            // when
            RecipeResponseDTO result = recipeService.getRecipeById(TEST_USER_ID, TEST_RECIPE_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(TEST_RECIPE_ID);
            assertThat(result.getUserId()).isEqualTo(TEST_USER_ID);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when recipe not found")
        void shouldThrowExceptionWhenRecipeNotFound() {
            // given
            when(recipeRepository.findByIdAndUserId(TEST_RECIPE_ID, TEST_USER_ID))
                    .thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> recipeService.getRecipeById(TEST_USER_ID, TEST_RECIPE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Recipe");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when recipe belongs to different user")
        void shouldThrowExceptionWhenRecipeBelongsToDifferentUser() {
            // given
            when(recipeRepository.findByIdAndUserId(TEST_RECIPE_ID, OTHER_USER_ID))
                    .thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> recipeService.getRecipeById(OTHER_USER_ID, TEST_RECIPE_ID))
                    .isInstanceOf(ResourceNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("searchRecipes")
    class SearchRecipes {

        @Test
        @DisplayName("should return paginated recipes with filter")
        void shouldReturnPaginatedRecipesWithFilter() {
            // given
            var nameFilter = new net.pvytykac.nutrition.common.filtering.StringFilter();
            nameFilter.setValue(List.of("Potato"));
            nameFilter.setOperator(net.pvytykac.nutrition.common.filtering.StringFilter.Operator.CONTAINS);
            
            RecipeFilter filter = RecipeFilter.builder()
                    .name(nameFilter)
                    .build();
            Pageable pageable = PageRequest.of(0, 20);
            Recipe recipe = createTestRecipe();
            Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

            when(recipeRepository.findAllByUserId(eq(TEST_USER_ID), any(Specification.class), eq(pageable)))
                    .thenReturn(recipePage);

            // when
            Page<RecipeResponseDTO> result = recipeService.searchRecipes(TEST_USER_ID, filter, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().getFirst().getName()).isEqualTo("Baked Potatoes");
        }

        @Test
        @DisplayName("should return paginated recipes without filter")
        void shouldReturnPaginatedRecipesWithoutFilter() {
            // given
            RecipeFilter filter = RecipeFilter.builder().build();
            Pageable pageable = PageRequest.of(0, 20);
            Recipe recipe = createTestRecipe();
            Page<Recipe> recipePage = new PageImpl<>(List.of(recipe));

            when(recipeRepository.findAllByUserId(TEST_USER_ID, pageable))
                    .thenReturn(recipePage);

            // when
            Page<RecipeResponseDTO> result = recipeService.searchRecipes(TEST_USER_ID, filter, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("should return empty page when no recipes found")
        void shouldReturnEmptyPageWhenNoRecipes() {
            // given
            RecipeFilter filter = RecipeFilter.builder().build();
            Pageable pageable = PageRequest.of(0, 20);
            Page<Recipe> emptyPage = new PageImpl<>(Collections.emptyList());

            when(recipeRepository.findAllByUserId(TEST_USER_ID, pageable))
                    .thenReturn(emptyPage);

            // when
            Page<RecipeResponseDTO> result = recipeService.searchRecipes(TEST_USER_ID, filter, pageable);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateRecipe")
    class UpdateRecipe {

        @Test
        @DisplayName("should update recipe successfully")
        void shouldUpdateRecipeSuccessfully() {
            // given
            Recipe existingRecipe = createTestRecipe();
            Ingredient ingredient = createTestIngredient();

            RecipeRequestDTO request = RecipeRequestDTO.builder()
                    .name("Updated Recipe Name")
                    .ingredients(List.of(
                            RecipeIngredientRequestDTO.builder()
                                    .ingredientId(TEST_INGREDIENT_ID)
                                    .multiplier(new BigDecimal("3.0"))
                                    .build()
                    ))
                    .build();

            when(recipeRepository.findByIdAndUserIdForUpdate(TEST_RECIPE_ID, TEST_USER_ID))
                    .thenReturn(Optional.of(existingRecipe));
            when(ingredientRepository.findById(TEST_INGREDIENT_ID)).thenReturn(Optional.of(ingredient));
            when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

            // when
            RecipeResponseDTO result = recipeService.updateRecipe(TEST_USER_ID, TEST_RECIPE_ID, request);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("Updated Recipe Name");
            assertThat(existingRecipe.getIngredients()).hasSize(1);
            assertThat(existingRecipe.getIngredients().getFirst().getMultiplier()).isEqualTo(new BigDecimal("3.0"));
        }

        @Test
        @DisplayName("should replace all ingredients on update")
        void shouldReplaceAllIngredientsOnUpdate() {
            // given
            Recipe existingRecipe = createTestRecipe();
            Ingredient oldIngredient = createTestIngredient();
            RecipeIngredient oldRecipeIngredient = RecipeIngredient.builder()
                    .id(UUID.randomUUID())
                    .recipe(existingRecipe)
                    .ingredient(oldIngredient)
                    .multiplier(new BigDecimal("1.0"))
                    .build();
            existingRecipe.getIngredients().add(oldRecipeIngredient);

            Ingredient newIngredient = Ingredient.builder()
                    .id(UUID.fromString("550e8400-e29b-41d4-a716-446655440002"))
                    .name("Butter")
                    .quantity(new BigDecimal("100.0"))
                    .unit(Unit.GRAM)
                    .nutritionDetails(NutritionDetails.builder().build())
                    .build();

            RecipeRequestDTO request = RecipeRequestDTO.builder()
                    .name("Updated Recipe")
                    .ingredients(List.of(
                            RecipeIngredientRequestDTO.builder()
                                    .ingredientId(newIngredient.getId())
                                    .multiplier(new BigDecimal("0.5"))
                                    .build()
                    ))
                    .build();

            when(recipeRepository.findByIdAndUserIdForUpdate(TEST_RECIPE_ID, TEST_USER_ID))
                    .thenReturn(Optional.of(existingRecipe));
            when(ingredientRepository.findById(newIngredient.getId())).thenReturn(Optional.of(newIngredient));
            when(recipeRepository.save(any(Recipe.class))).thenReturn(existingRecipe);

            // when
            recipeService.updateRecipe(TEST_USER_ID, TEST_RECIPE_ID, request);

            // then
            assertThat(existingRecipe.getIngredients()).hasSize(1);
            assertThat(existingRecipe.getIngredients().getFirst().getIngredient().getName()).isEqualTo("Butter");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when recipe not found")
        void shouldThrowExceptionWhenRecipeNotFound() {
            // given
            RecipeRequestDTO request = RecipeRequestDTO.builder()
                    .name("Updated Recipe")
                    .ingredients(Collections.emptyList())
                    .build();

            when(recipeRepository.findByIdAndUserIdForUpdate(TEST_RECIPE_ID, TEST_USER_ID))
                    .thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> recipeService.updateRecipe(TEST_USER_ID, TEST_RECIPE_ID, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Recipe");
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when ingredient not found during update")
        void shouldThrowExceptionWhenIngredientNotFound() {
            // given
            Recipe existingRecipe = createTestRecipe();
            RecipeRequestDTO request = RecipeRequestDTO.builder()
                    .name("Updated Recipe")
                    .ingredients(List.of(
                            RecipeIngredientRequestDTO.builder()
                                    .ingredientId(TEST_INGREDIENT_ID)
                                    .multiplier(new BigDecimal("2.0"))
                                    .build()
                    ))
                    .build();

            when(recipeRepository.findByIdAndUserIdForUpdate(TEST_RECIPE_ID, TEST_USER_ID))
                    .thenReturn(Optional.of(existingRecipe));
            when(ingredientRepository.findById(TEST_INGREDIENT_ID)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> recipeService.updateRecipe(TEST_USER_ID, TEST_RECIPE_ID, request))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Ingredient");
        }
    }

    @Nested
    @DisplayName("deleteRecipe")
    class DeleteRecipe {

        @Test
        @DisplayName("should delete recipe successfully")
        void shouldDeleteRecipeSuccessfully() {
            // given
            Recipe recipe = createTestRecipe();
            when(recipeRepository.findByIdAndUserIdForUpdate(TEST_RECIPE_ID, TEST_USER_ID))
                    .thenReturn(Optional.of(recipe));

            // when
            recipeService.deleteRecipe(TEST_USER_ID, TEST_RECIPE_ID);

            // then
            verify(recipeRepository).delete(recipe);
        }

        @Test
        @DisplayName("should throw ResourceNotFoundException when recipe not found")
        void shouldThrowExceptionWhenRecipeNotFound() {
            // given
            when(recipeRepository.findByIdAndUserIdForUpdate(TEST_RECIPE_ID, TEST_USER_ID))
                    .thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> recipeService.deleteRecipe(TEST_USER_ID, TEST_RECIPE_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("Recipe");
        }
    }

    @Nested
    @DisplayName("mapToRecipeIngredientResponse")
    class MapToRecipeIngredientResponse {

        @Test
        @DisplayName("should calculate nutritional values correctly")
        void shouldCalculateNutritionalValuesCorrectly() {
            // given
            Recipe recipe = createTestRecipe();
            Ingredient ingredient = createTestIngredient();
            RecipeIngredient recipeIngredient = RecipeIngredient.builder()
                    .id(UUID.randomUUID())
                    .recipe(recipe)
                    .ingredient(ingredient)
                    .multiplier(new BigDecimal("2.5"))
                    .build();

            RecipeRequestDTO request = RecipeRequestDTO.builder()
                    .name("Test Recipe")
                    .ingredients(List.of(
                            RecipeIngredientRequestDTO.builder()
                                    .ingredientId(TEST_INGREDIENT_ID)
                                    .multiplier(new BigDecimal("2.5"))
                                    .build()
                    ))
                    .build();

            when(ingredientRepository.findById(TEST_INGREDIENT_ID)).thenReturn(Optional.of(ingredient));
            when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            RecipeResponseDTO result = recipeService.createRecipe(TEST_USER_ID, request);

            // then
            RecipeIngredientResponseDTO ri = result.getIngredients().getFirst();
            assertThat(ri.getBaseQuantity()).isEqualByComparingTo(new BigDecimal("100.0"));
            assertThat(ri.getMultiplier()).isEqualByComparingTo(new BigDecimal("2.5"));
            assertThat(ri.getCalculatedQuantity()).isEqualByComparingTo(new BigDecimal("250.0"));
            assertThat(ri.getFatContent()).isEqualByComparingTo(new BigDecimal("0.25"));
            assertThat(ri.getCarbsContent()).isEqualByComparingTo(new BigDecimal("42.5"));
            assertThat(ri.getProteinContent()).isEqualByComparingTo(new BigDecimal("5.0"));
            assertThat(ri.getPhenylalanineContent()).isEqualByComparingTo(new BigDecimal("125.0"));
            assertThat(ri.getKilocalories()).isEqualByComparingTo(new BigDecimal("192.5"));
        }

        @Test
        @DisplayName("should handle null nutritional values")
        void shouldHandleNullNutritionalValues() {
            // given
            Recipe recipe = createTestRecipe();
            Ingredient ingredient = Ingredient.builder()
                    .id(TEST_INGREDIENT_ID)
                    .name("Ingredient With Null Nutrition")
                    .quantity(new BigDecimal("100.0"))
                    .unit(Unit.GRAM)
                    .nutritionDetails(NutritionDetails.builder().build())
                    .build();

            RecipeRequestDTO request = RecipeRequestDTO.builder()
                    .name("Test Recipe")
                    .ingredients(List.of(
                            RecipeIngredientRequestDTO.builder()
                                    .ingredientId(TEST_INGREDIENT_ID)
                                    .multiplier(new BigDecimal("2.0"))
                                    .build()
                    ))
                    .build();

            when(ingredientRepository.findById(TEST_INGREDIENT_ID)).thenReturn(Optional.of(ingredient));
            when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            RecipeResponseDTO result = recipeService.createRecipe(TEST_USER_ID, request);

            // then
            RecipeIngredientResponseDTO ri = result.getIngredients().getFirst();
            assertThat(ri.getFatContent()).isNull();
            assertThat(ri.getCarbsContent()).isNull();
            assertThat(ri.getProteinContent()).isNull();
            assertThat(ri.getPhenylalanineContent()).isNull();
            assertThat(ri.getKilocalories()).isNull();
        }
    }
}
