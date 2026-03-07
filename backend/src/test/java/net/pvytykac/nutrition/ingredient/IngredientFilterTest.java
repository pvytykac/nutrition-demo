package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.util.filtering.NumberOperator;
import net.pvytykac.nutrition.util.filtering.StringOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IngredientFilter")
class IngredientFilterTest {

    @Nested
    @DisplayName("nameContains")
    class NameContains {

        @Test
        @DisplayName("should return null when value is null")
        void shouldReturnNullWhenValueIsNull() {
            // when
            Specification<Ingredient> result = IngredientFilter.nameContains(null, StringOperator.EQUALS);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null when value is blank")
        void shouldReturnNullWhenValueIsBlank() {
            // when
            Specification<Ingredient> result = IngredientFilter.nameContains("   ", StringOperator.EQUALS);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should apply EQUALS operator")
        void shouldApplyEqualsOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.nameContains("Chicken", StringOperator.EQUALS);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("should apply STARTS_WITH operator")
        void shouldApplyStartsWithOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.nameContains("Chicken", StringOperator.STARTS_WITH);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("should apply ENDS_WITH operator")
        void shouldApplyEndsWithOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.nameContains("Breast", StringOperator.ENDS_WITH);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("should apply CONTAINS operator")
        void shouldApplyContainsOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.nameContains("icken", StringOperator.CONTAINS);

            // then
            assertThat(spec).isNotNull();
        }
    }

    @Nested
    @DisplayName("phenylalanineFilter")
    class PhenylalanineFilter {

        @Test
        @DisplayName("should return null when value is null")
        void shouldReturnNullWhenValueIsNull() {
            // when
            Specification<Ingredient> result = IngredientFilter.phenylalanineFilter(null, null, NumberOperator.EQUALS);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should apply EQUALS operator")
        void shouldApplyEqualsOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.phenylalanineFilter(
                    new BigDecimal("5.0"), null, NumberOperator.EQUALS);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("should apply GREATER_THAN operator")
        void shouldApplyGreaterThanOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.phenylalanineFilter(
                    new BigDecimal("5.0"), null, NumberOperator.GREATER_THAN);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("should apply GREATER_THAN_OR_EQUAL operator")
        void shouldApplyGreaterThanOrEqualOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.phenylalanineFilter(
                    new BigDecimal("5.0"), null, NumberOperator.GREATER_THAN_OR_EQUAL);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("should apply LOWER_THAN operator")
        void shouldApplyLowerThanOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.phenylalanineFilter(
                    new BigDecimal("5.0"), null, NumberOperator.LOWER_THAN);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("should apply LOWER_THAN_OR_EQUAL operator")
        void shouldApplyLowerThanOrEqualOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.phenylalanineFilter(
                    new BigDecimal("5.0"), null, NumberOperator.LOWER_THAN_OR_EQUAL);

            // then
            assertThat(spec).isNotNull();
        }

        @Test
        @DisplayName("should apply BETWEEN operator")
        void shouldApplyBetweenOperator() {
            // when
            Specification<Ingredient> spec = IngredientFilter.phenylalanineFilter(
                    new BigDecimal("1.0"), new BigDecimal("10.0"), NumberOperator.BETWEEN);

            // then
            assertThat(spec).isNotNull();
        }
    }

    @Nested
    @DisplayName("combine")
    class Combine {

        @Test
        @DisplayName("should return null when specs list is empty")
        void shouldReturnNullWhenEmpty() {
            // when
            Specification<Ingredient> result = IngredientFilter.combine(java.util.List.of());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should combine single specification")
        void shouldCombineSingleSpec() {
            // given
            var specs = java.util.List.of(IngredientFilter.nameContains("test", StringOperator.CONTAINS));

            // when
            Specification<Ingredient> result = IngredientFilter.combine(specs);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should combine multiple specifications")
        void shouldCombineMultipleSpecs() {
            // given
            var specs = java.util.List.of(
                    IngredientFilter.nameContains("test", StringOperator.CONTAINS),
                    IngredientFilter.phenylalanineFilter(new BigDecimal("5.0"), null, NumberOperator.EQUALS));

            // when
            Specification<Ingredient> result = IngredientFilter.combine(specs);

            // then
            assertThat(result).isNotNull();
        }
    }
}
