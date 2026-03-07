package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.RepositoryTestBase;
import net.pvytykac.nutrition.util.filtering.NumberOperator;
import net.pvytykac.nutrition.util.filtering.StringOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IngredientRepository")
class IngredientRepositoryTest extends RepositoryTestBase {

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

        testEntityManager.persistFlushFind(ingredient);

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

        testEntityManager.persistFlushFind(ingredient);

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

        testEntityManager.persistFlushFind(Ingredient.builder().name("Ingredient1").nutritionDetails(nutrition1).build());
        testEntityManager.persistFlushFind(Ingredient.builder().name("Ingredient2").nutritionDetails(nutrition2).build());

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
                .unit("1g")
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("ToDelete")
                .nutritionDetails(nutritionDetails)
                .build();

        Ingredient saved = testEntityManager.persistFlushFind(ingredient);
        UUID id = saved.getId();

        // when
        ingredientRepository.deleteById(id);

        // then
        assertThat(ingredientRepository.findById(id)).isEmpty();
    }

    @Test
    @DisplayName("should find ingredients by name using CONTAINS filter")
    void shouldFindByNameContainsFilter() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var spec = IngredientFilter.nameContains("Ap", StringOperator.CONTAINS);
        var result = ingredientRepository.findAll(spec);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by name using STARTS_WITH filter")
    void shouldFindByNameStartsWithFilter() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var spec = IngredientFilter.nameContains("Ap", StringOperator.STARTS_WITH);
        var result = ingredientRepository.findAll(spec);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by name using ENDS_WITH filter")
    void shouldFindByNameEndsWithFilter() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var spec = IngredientFilter.nameContains("ple", StringOperator.ENDS_WITH);
        var result = ingredientRepository.findAll(spec);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by name using EQUALS filter")
    void shouldFindByNameEqualsFilter() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var spec = IngredientFilter.nameContains("Apple", StringOperator.EQUALS);
        var result = ingredientRepository.findAll(spec);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by phenylalanine using GREATER_THAN filter")
    void shouldFindByPhenylalanineGreaterThan() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var spec = IngredientFilter.phenylalanineFilter(
                new BigDecimal("1.0"), null, NumberOperator.GREATER_THAN);
        var result = ingredientRepository.findAll(spec);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Banana");
    }

    @Test
    @DisplayName("should find ingredients by phenylalanine using LOWER_THAN filter")
    void shouldFindByPhenylalanineLowerThan() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var spec = IngredientFilter.phenylalanineFilter(
                new BigDecimal("1.0"), null, NumberOperator.LOWER_THAN);
        var result = ingredientRepository.findAll(spec);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by phenylalanine using BETWEEN filter")
    void shouldFindByPhenylalanineBetween() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        // Apple has 0.1, Banana has 2.0 - between 0 and 3 should return both
        var spec = IngredientFilter.phenylalanineFilter(
                new BigDecimal("0.0"), new BigDecimal("3.0"), NumberOperator.BETWEEN);
        var result = ingredientRepository.findAll(spec);
        
        // then
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("should find ingredients using combined filters")
    void shouldFindUsingCombinedFilters() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var nameSpec = IngredientFilter.nameContains("Apple", StringOperator.EQUALS);
        var phenylSpec = IngredientFilter.phenylalanineFilter(
                new BigDecimal("0.1"), null, NumberOperator.EQUALS);
        var combinedSpec = IngredientFilter.combine(List.of(nameSpec, phenylSpec));
        var result = ingredientRepository.findAll(combinedSpec);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Apple");
    }

    private void saveTestIngredients() {
        // Apple - low phenylalanine
        testEntityManager.persistFlushFind(Ingredient.builder()
                .name("Apple")
                .nutritionDetails(NutritionDetails.builder()
                        .fat(new BigDecimal("0.3"))
                        .carbs(new BigDecimal("25.0"))
                        .protein(new BigDecimal("0.5"))
                        .phenylalanine(new BigDecimal("0.1"))
                        .unit("100g")
                        .build())
                .build());
        
        // Banana - higher phenylalanine
        testEntityManager.persistFlushFind(Ingredient.builder()
                .name("Banana")
                .nutritionDetails(NutritionDetails.builder()
                        .fat(new BigDecimal("0.2"))
                        .carbs(new BigDecimal("27.0"))
                        .protein(new BigDecimal("1.0"))
                        .phenylalanine(new BigDecimal("2.0"))
                        .unit("100g")
                        .build())
                .build());
    }
}
