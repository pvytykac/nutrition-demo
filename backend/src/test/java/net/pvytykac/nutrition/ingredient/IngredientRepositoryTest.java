package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.RepositoryTestBase;
import net.pvytykac.nutrition.util.filtering.NumericFilter;
import net.pvytykac.nutrition.util.filtering.StringFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;
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
                .fatContent(new BigDecimal("10.5"))
                .carbsContent(new BigDecimal("20.0"))
                .proteinContent(new BigDecimal("15.0"))
                .phenylalanineContent(new BigDecimal("5.0"))
                .kilocalories(new BigDecimal("150.0"))
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("Chicken Breast")
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(nutritionDetails)
                .build();

        // when
        Ingredient saved = ingredientRepository.save(ingredient);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Chicken Breast");
        assertThat(saved.getQuantity()).isEqualByComparingTo(new BigDecimal("100.0"));
        assertThat(saved.getUnit()).isEqualTo(Unit.GRAM);
        assertThat(saved.getNutritionDetails()).isNotNull();
        assertThat(saved.getNutritionDetails().getFatContent()).isEqualByComparingTo(new BigDecimal("10.5"));
        assertThat(saved.getNutritionDetails().getCarbsContent()).isEqualByComparingTo(new BigDecimal("20.0"));
        assertThat(saved.getNutritionDetails().getProteinContent()).isEqualByComparingTo(new BigDecimal("15.0"));
        assertThat(saved.getNutritionDetails().getPhenylalanineContent()).isEqualByComparingTo(new BigDecimal("5.0"));
        assertThat(saved.getNutritionDetails().getKilocalories()).isEqualByComparingTo(new BigDecimal("150.0"));
    }

    @Test
    @DisplayName("should retrieve all ingredients")
    void shouldRetrieveAllIngredients() {
        // given
        NutritionDetails nutrition1 = NutritionDetails.builder()
                .fatContent(new BigDecimal("1.0"))
                .carbsContent(new BigDecimal("2.0"))
                .proteinContent(new BigDecimal("3.0"))
                .phenylalanineContent(new BigDecimal("0.5"))
                .kilocalories(new BigDecimal("50.0"))
                .build();

        NutritionDetails nutrition2 = NutritionDetails.builder()
                .fatContent(new BigDecimal("5.0"))
                .carbsContent(new BigDecimal("10.0"))
                .proteinContent(new BigDecimal("8.0"))
                .phenylalanineContent(new BigDecimal("2.0"))
                .kilocalories(new BigDecimal("100.0"))
                .build();

        testEntityManager.persistFlushFind(Ingredient.builder()
                .name("Ingredient1")
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(nutrition1)
                .build());
        testEntityManager.persistFlushFind(Ingredient.builder()
                .name("Ingredient2")
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(nutrition2)
                .build());

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
                .fatContent(new BigDecimal("1.0"))
                .carbsContent(new BigDecimal("2.0"))
                .proteinContent(new BigDecimal("3.0"))
                .phenylalanineContent(new BigDecimal("0.5"))
                .kilocalories(new BigDecimal("50.0"))
                .build();

        Ingredient ingredient = Ingredient.builder()
                .name("ToDelete")
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
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
        var nameFilter = new StringFilter();
        nameFilter.setValue(List.of("Ap"));
        nameFilter.setOperator(StringFilter.Operator.CONTAINS);
        
        var filter = IngredientFilter.builder()
                .nameFilter(nameFilter)
                .build();
        
        var result = ingredientRepository.findAll(filter.toSpecification());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by name using STARTS_WITH filter")
    void shouldFindByNameStartsWithFilter() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var nameFilter = new StringFilter();
        nameFilter.setValue(List.of("Ap"));
        nameFilter.setOperator(StringFilter.Operator.STARTS_WITH);
        
        var filter = IngredientFilter.builder()
                .nameFilter(nameFilter)
                .build();
        
        var result = ingredientRepository.findAll(filter.toSpecification());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by name using ENDS_WITH filter")
    void shouldFindByNameEndsWithFilter() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var nameFilter = new StringFilter();
        nameFilter.setValue(List.of("ple"));
        nameFilter.setOperator(StringFilter.Operator.ENDS_WITH);
        
        var filter = IngredientFilter.builder()
                .nameFilter(nameFilter)
                .build();
        
        var result = ingredientRepository.findAll(filter.toSpecification());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by name using EXACT_MATCH filter")
    void shouldFindByNameExactMatchFilter() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var nameFilter = new StringFilter();
        nameFilter.setValue(List.of("Apple"));
        nameFilter.setOperator(StringFilter.Operator.EXACT_MATCH);
        
        var filter = IngredientFilter.builder()
                .nameFilter(nameFilter)
                .build();
        
        var result = ingredientRepository.findAll(filter.toSpecification());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by phenylalanine using GREATER_THAN filter")
    void shouldFindByPhenylalanineGreaterThan() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var phenylFilter = new NumericFilter();
        phenylFilter.setValue(List.of(new BigDecimal("1.0")));
        phenylFilter.setOperator(NumericFilter.Operator.GREATER_THAN);
        
        var filter = IngredientFilter.builder()
                .phenylalanineContentFilter(phenylFilter)
                .build();
        
        var result = ingredientRepository.findAll(filter.toSpecification());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Banana");
    }

    @Test
    @DisplayName("should find ingredients by phenylalanine using LOWER_THAN filter")
    void shouldFindByPhenylalanineLowerThan() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        var phenylFilter = new NumericFilter();
        phenylFilter.setValue(List.of(new BigDecimal("1.0")));
        phenylFilter.setOperator(NumericFilter.Operator.LOWER_THAN);
        
        var filter = IngredientFilter.builder()
                .phenylalanineContentFilter(phenylFilter)
                .build();
        
        var result = ingredientRepository.findAll(filter.toSpecification());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Apple");
    }

    @Test
    @DisplayName("should find ingredients by phenylalanine using BETWEEN filter")
    void shouldFindByPhenylalanineBetween() {
        // given
        ingredientRepository.deleteAll();
        saveTestIngredients();
        
        // when
        // Apple has 0.1, Banana has 2.0 - between 0 and 3 should return both
        var phenylFilter = new NumericFilter();
        phenylFilter.setValue(List.of(new BigDecimal("0.0"), new BigDecimal("3.0")));
        phenylFilter.setOperator(NumericFilter.Operator.BETWEEN);
        
        var filter = IngredientFilter.builder()
                .phenylalanineContentFilter(phenylFilter)
                .build();
        
        var result = ingredientRepository.findAll(filter.toSpecification());
        
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
        var nameFilter = new StringFilter();
        nameFilter.setValue(List.of("Apple"));
        nameFilter.setOperator(StringFilter.Operator.EXACT_MATCH);
        
        var phenylFilter = new NumericFilter();
        phenylFilter.setValue(List.of(new BigDecimal("0.1")));
        phenylFilter.setOperator(NumericFilter.Operator.EQUAL);
        
        var filter = IngredientFilter.builder()
                .nameFilter(nameFilter)
                .phenylalanineContentFilter(phenylFilter)
                .build();
        
        var result = ingredientRepository.findAll(filter.toSpecification());
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Apple");
    }

    private void saveTestIngredients() {
        // Apple - low phenylalanine
        testEntityManager.persistFlushFind(Ingredient.builder()
                .name("Apple")
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(NutritionDetails.builder()
                        .fatContent(new BigDecimal("0.3"))
                        .carbsContent(new BigDecimal("25.0"))
                        .proteinContent(new BigDecimal("0.5"))
                        .phenylalanineContent(new BigDecimal("0.1"))
                        .kilocalories(new BigDecimal("50.0"))
                        .build())
                .build());
        
        // Banana - higher phenylalanine
        testEntityManager.persistFlushFind(Ingredient.builder()
                .name("Banana")
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(NutritionDetails.builder()
                        .fatContent(new BigDecimal("0.2"))
                        .carbsContent(new BigDecimal("27.0"))
                        .proteinContent(new BigDecimal("1.0"))
                        .phenylalanineContent(new BigDecimal("2.0"))
                        .kilocalories(new BigDecimal("90.0"))
                        .build())
                .build());
    }
}
