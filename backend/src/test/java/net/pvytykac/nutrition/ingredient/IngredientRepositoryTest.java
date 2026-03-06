package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.config.IntegrationTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IngredientRepository")
class IngredientRepositoryTest extends IntegrationTestBase {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Test
    @DisplayName("should save and retrieve ingredient")
    void shouldSaveAndRetrieveIngredient() {
        // given
        NutritionDetails nutritionDetails = NutritionDetails.builder()
                .fat(new BigDecimal("10.5"))
                .carbs(new BigDecimal("20.0"))
                .protein(new BigDecimal("15.0"))
                .phenylalanine(new BigDecimal("5.0"))
                .unit("100g")
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("Chicken Breast")
                .nutritionDetails(nutritionDetails)
                .build();

        // when
        Ingredient saved = ingredientRepository.save(ingredient);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Chicken Breast");
        assertThat(saved.getNutritionDetails()).isNotNull();
        assertThat(saved.getNutritionDetails().getFat()).isEqualByComparingTo(new BigDecimal("10.5"));
        assertThat(saved.getNutritionDetails().getCarbs()).isEqualByComparingTo(new BigDecimal("20.0"));
        assertThat(saved.getNutritionDetails().getProtein()).isEqualByComparingTo(new BigDecimal("15.0"));
        assertThat(saved.getNutritionDetails().getPhenylalanine()).isEqualByComparingTo(new BigDecimal("5.0"));
        assertThat(saved.getNutritionDetails().getUnit()).isEqualTo("100g");
    }

    @Test
    @DisplayName("should find ingredient by name")
    void shouldFindIngredientByName() {
        // given
        NutritionDetails nutritionDetails = NutritionDetails.builder()
                .fat(new BigDecimal("5.0"))
                .carbs(new BigDecimal("10.0"))
                .protein(new BigDecimal("8.0"))
                .phenylalanine(new BigDecimal("2.0"))
                .unit("100ml")
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("Apple Juice")
                .nutritionDetails(nutritionDetails)
                .build();

        ingredientRepository.save(ingredient);

        // when
        Optional<Ingredient> result = ingredientRepository.findByName("Apple Juice");

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Apple Juice");
    }

    @Test
    @DisplayName("should return empty when finding non-existent name")
    void shouldReturnEmptyWhenFindingNonExistentName() {
        // when
        Optional<Ingredient> result = ingredientRepository.findByName("NonExistent");

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should check if ingredient exists by name")
    void shouldCheckIfIngredientExistsByName() {
        // given
        NutritionDetails nutritionDetails = NutritionDetails.builder()
                .fat(new BigDecimal("1.0"))
                .carbs(new BigDecimal("2.0"))
                .protein(new BigDecimal("3.0"))
                .phenylalanine(new BigDecimal("0.5"))
                .unit("1g")
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("Salt")
                .nutritionDetails(nutritionDetails)
                .build();

        ingredientRepository.save(ingredient);

        // when / then
        assertThat(ingredientRepository.existsByName("Salt")).isTrue();
        assertThat(ingredientRepository.existsByName("NonExistent")).isFalse();
    }

    @Test
    @DisplayName("should retrieve all ingredients")
    void shouldRetrieveAllIngredients() {
        // given
        NutritionDetails nutrition1 = NutritionDetails.builder()
                .fat(new BigDecimal("1.0"))
                .carbs(new BigDecimal("2.0"))
                .protein(new BigDecimal("3.0"))
                .phenylalanine(new BigDecimal("0.5"))
                .unit("100g")
                .build();

        NutritionDetails nutrition2 = NutritionDetails.builder()
                .fat(new BigDecimal("5.0"))
                .carbs(new BigDecimal("10.0"))
                .protein(new BigDecimal("8.0"))
                .phenylalanine(new BigDecimal("2.0"))
                .unit("100g")
                .build();

        ingredientRepository.save(Ingredient.builder().name("Ingredient1").nutritionDetails(nutrition1).build());
        ingredientRepository.save(Ingredient.builder().name("Ingredient2").nutritionDetails(nutrition2).build());

        // when
        var result = ingredientRepository.findAll();

        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("should delete ingredient by id")
    void shouldDeleteIngredientById() {
        // given
        NutritionDetails nutritionDetails = NutritionDetails.builder()
                .fat(new BigDecimal("1.0"))
                .carbs(new BigDecimal("2.0"))
                .protein(new BigDecimal("3.0"))
                .phenylalanine(new BigDecimal("0.5"))
                .unit("100g")
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("ToDelete")
                .nutritionDetails(nutritionDetails)
                .build();

        Ingredient saved = ingredientRepository.save(ingredient);
        Long id = saved.getId();

        // when
        ingredientRepository.deleteById(id);

        // then
        assertThat(ingredientRepository.findById(id)).isEmpty();
    }
}
