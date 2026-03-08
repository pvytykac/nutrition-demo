package net.pvytykac.nutrition.common.filtering;

import net.pvytykac.nutrition.RepositoryTestBase;
import net.pvytykac.nutrition.ingredient.Ingredient;
import net.pvytykac.nutrition.ingredient.NutritionDetails;
import net.pvytykac.nutrition.ingredient.Unit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SpecificationBuilder Integration")
class SpecificationBuilderIntegrationTest extends RepositoryTestBase {

    @Autowired
    private net.pvytykac.nutrition.ingredient.IngredientRepository ingredientRepository;

    @BeforeEach
    void setUp() {
        ingredientRepository.deleteAll();
    }

    private void saveTestIngredient(String name, BigDecimal quantity, Unit unit, 
                                     BigDecimal fat, BigDecimal carbs, BigDecimal protein, 
                                     BigDecimal phenylalanine) {
        testEntityManager.persistFlushFind(Ingredient.builder()
                .name(name)
                .quantity(quantity)
                .unit(unit)
                .nutritionDetails(NutritionDetails.builder()
                        .fatContent(fat)
                        .carbsContent(carbs)
                        .proteinContent(protein)
                        .phenylalanineContent(phenylalanine)
                        .kilocalories(new BigDecimal("100.0"))
                        .build())
                .build());
    }

    @Nested
    @DisplayName("stringFilter operators")
    class StringFilterOperators {

        @Test
        @DisplayName("should filter by EXACT_MATCH")
        void shouldFilterByExactMatch() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM, 
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));

            var filter = new StringFilter();
            filter.setValue(List.of("Apple"));
            filter.setOperator(StringFilter.Operator.EXACT_MATCH);

            Specification<Ingredient> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get("name")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by STARTS_WITH")
        void shouldFilterByStartsWith() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));

            var filter = new StringFilter();
            filter.setValue(List.of("App"));
            filter.setOperator(StringFilter.Operator.STARTS_WITH);

            Specification<Ingredient> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get("name")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by ENDS_WITH")
        void shouldFilterByEndsWith() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("11.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));

            var filter = new StringFilter();
            filter.setValue(List.of("le"));
            filter.setOperator(StringFilter.Operator.ENDS_WITH);

            Specification<Ingredient> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get("name")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by CONTAINS")
        void shouldFilterByContains() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));

            var filter = new StringFilter();
            filter.setValue(List.of("pp"));
            filter.setOperator(StringFilter.Operator.CONTAINS);

            Specification<Ingredient> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get("name")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by IN")
        void shouldFilterByIn() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("1.0"));

            var filter = new StringFilter();
            filter.setValue(List.of("Apple"));
            filter.setOperator(StringFilter.Operator.IN);

            Specification<Ingredient> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get("name")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }
    }

    @Nested
    @DisplayName("numericFilter operators")
    class NumericFilterOperators {

        @Test
        @DisplayName("should filter by EQUAL")
        void shouldFilterByEqual() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("20.0"));

            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.EQUAL);

            Specification<Ingredient> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get("nutritionDetails").get("phenylalanineContent")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by GREATER_THAN")
        void shouldFilterByGreaterThan() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("20.0"));

            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("15.0")));
            filter.setOperator(NumericFilter.Operator.GREATER_THAN);

            Specification<Ingredient> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get("nutritionDetails").get("phenylalanineContent")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Banana");
        }

        @Test
        @DisplayName("should filter by GREATER_THAN_OR_EQUAL")
        void shouldFilterByGreaterThanOrEqual() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("20.0"));

            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("20.0")));
            filter.setOperator(NumericFilter.Operator.GREATER_THAN_OR_EQUAL);

            Specification<Ingredient> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get("nutritionDetails").get("phenylalanineContent")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Banana");
        }

        @Test
        @DisplayName("should filter by LOWER_THAN")
        void shouldFilterByLowerThan() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("20.0"));

            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("15.0")));
            filter.setOperator(NumericFilter.Operator.LOWER_THAN);

            Specification<Ingredient> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get("nutritionDetails").get("phenylalanineContent")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by LOWER_THAN_OR_EQUAL")
        void shouldFilterByLowerThanOrEqual() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("20.0"));

            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.LOWER_THAN_OR_EQUAL);

            Specification<Ingredient> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get("nutritionDetails").get("phenylalanineContent")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by BETWEEN")
        void shouldFilterByBetween() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));
            saveTestIngredient("Banana", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("20.0"));
            saveTestIngredient("Cherry", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("30.0"));

            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("15.0"), new BigDecimal("25.0")));
            filter.setOperator(NumericFilter.Operator.BETWEEN);

            Specification<Ingredient> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get("nutritionDetails").get("phenylalanineContent")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Banana");
        }

        @Test
        @DisplayName("should handle BETWEEN with null second value")
        void shouldHandleBetweenWithNullSecondValue() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));

            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.BETWEEN);

            Specification<Ingredient> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get("nutritionDetails").get("phenylalanineContent")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }
    }

    @Nested
    @DisplayName("enumFilter operators")
    class EnumFilterOperators {

        @Test
        @DisplayName("should filter by IN")
        void shouldFilterByIn() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));
            saveTestIngredient("Water", new BigDecimal("100"), Unit.MILLILITER,
                    new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"));

            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of(Unit.GRAM));
            filter.setOperator(EnumFilter.Operator.IN);

            Specification<Ingredient> spec = SpecificationBuilder.enumFilter(
                    filter,
                    (root) -> root.get("unit")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getUnit()).isEqualTo(Unit.GRAM);
        }

        @Test
        @DisplayName("should filter by NOT_IN")
        void shouldFilterByNotIn() {
            // given
            saveTestIngredient("Apple", new BigDecimal("100"), Unit.GRAM,
                    new BigDecimal("1.0"), new BigDecimal("10.0"), new BigDecimal("5.0"), new BigDecimal("10.0"));
            saveTestIngredient("Water", new BigDecimal("100"), Unit.MILLILITER,
                    new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"));

            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of(Unit.GRAM));
            filter.setOperator(EnumFilter.Operator.NOT_IN);

            Specification<Ingredient> spec = SpecificationBuilder.enumFilter(
                    filter,
                    (root) -> root.get("unit")
            );

            // when
            var result = ingredientRepository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getUnit()).isEqualTo(Unit.MILLILITER);
        }
    }
}
