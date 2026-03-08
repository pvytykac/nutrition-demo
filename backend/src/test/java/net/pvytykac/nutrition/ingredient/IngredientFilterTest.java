package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.common.filtering.NumericFilter;
import net.pvytykac.nutrition.common.filtering.StringFilter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("IngredientFilter")
class IngredientFilterTest {

    @Nested
    @DisplayName("toSpecification")
    class ToSpecification {

        @Test
        @DisplayName("should return null when all filters are null")
        void shouldReturnNullWhenAllFiltersNull() {
            // given
            var filter = IngredientFilter.builder()
                    .nameFilter(null)
                    .fatContentFilter(null)
                    .proteinContentFilter(null)
                    .carbsContentFilter(null)
                    .phenylalanineContentFilter(null)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null when all filters are inactive")
        void shouldReturnNullWhenAllFiltersInactive() {
            // given
            var nameFilter = new StringFilter();
            nameFilter.setValue(null);

            var filter = IngredientFilter.builder()
                    .nameFilter(nameFilter)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should create specification with name filter only")
        void shouldCreateSpecificationWithNameFilter() {
            // given
            var nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Apple"));
            nameFilter.setOperator(StringFilter.Operator.STARTS_WITH);

            var filter = IngredientFilter.builder()
                    .nameFilter(nameFilter)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with phenylalanine filter only")
        void shouldCreateSpecificationWithPhenylalanineFilter() {
            // given
            var phenylFilter = new NumericFilter();
            phenylFilter.setValue(List.of(new BigDecimal("50.0")));
            phenylFilter.setOperator(NumericFilter.Operator.GREATER_THAN);

            var filter = IngredientFilter.builder()
                    .phenylalanineContentFilter(phenylFilter)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with multiple filters")
        void shouldCreateSpecificationWithMultipleFilters() {
            // given
            var nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Apple"));
            nameFilter.setOperator(StringFilter.Operator.CONTAINS);

            var phenylFilter = new NumericFilter();
            phenylFilter.setValue(List.of(new BigDecimal("50.0")));
            phenylFilter.setOperator(NumericFilter.Operator.LOWER_THAN);

            var filter = IngredientFilter.builder()
                    .nameFilter(nameFilter)
                    .phenylalanineContentFilter(phenylFilter)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("isActive methods")
    class IsActiveMethods {

        @Test
        @DisplayName("should detect active name filter")
        void shouldDetectActiveNameFilter() {
            // given
            var nameFilter = new StringFilter();
            nameFilter.setValue(List.of("Apple"));

            var filter = IngredientFilter.builder()
                    .nameFilter(nameFilter)
                    .build();

            // then
            assertThat(filter.isNameFilterActive()).isTrue();
            assertThat(filter.isFatContentFilterActive()).isFalse();
        }

        @Test
        @DisplayName("should detect inactive name filter")
        void shouldDetectInactiveNameFilter() {
            // given
            var nameFilter = new StringFilter();
            nameFilter.setValue(null);

            var filter = IngredientFilter.builder()
                    .nameFilter(nameFilter)
                    .build();

            // then
            assertThat(filter.isNameFilterActive()).isFalse();
        }

        @Test
        @DisplayName("should create specification with fat content filter")
        void shouldCreateSpecificationWithFatContentFilter() {
            // given
            var fatFilter = new NumericFilter();
            fatFilter.setValue(List.of(new BigDecimal("10.0")));
            fatFilter.setOperator(NumericFilter.Operator.GREATER_THAN);

            var filter = IngredientFilter.builder()
                    .fatContentFilter(fatFilter)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with protein content filter")
        void shouldCreateSpecificationWithProteinContentFilter() {
            // given
            var proteinFilter = new NumericFilter();
            proteinFilter.setValue(List.of(new BigDecimal("5.0")));
            proteinFilter.setOperator(NumericFilter.Operator.EQUAL);

            var filter = IngredientFilter.builder()
                    .proteinContentFilter(proteinFilter)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with carbs content filter")
        void shouldCreateSpecificationWithCarbsContentFilter() {
            // given
            var carbsFilter = new NumericFilter();
            carbsFilter.setValue(List.of(new BigDecimal("20.0")));
            carbsFilter.setOperator(NumericFilter.Operator.LOWER_THAN);

            var filter = IngredientFilter.builder()
                    .carbsContentFilter(carbsFilter)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification with all nutrition filters")
        void shouldCreateSpecificationWithAllNutritionFilters() {
            // given
            var fatFilter = new NumericFilter();
            fatFilter.setValue(List.of(new BigDecimal("1.0")));
            fatFilter.setOperator(NumericFilter.Operator.GREATER_THAN);

            var proteinFilter = new NumericFilter();
            proteinFilter.setValue(List.of(new BigDecimal("5.0")));
            proteinFilter.setOperator(NumericFilter.Operator.EQUAL);

            var carbsFilter = new NumericFilter();
            carbsFilter.setValue(List.of(new BigDecimal("20.0")));
            carbsFilter.setOperator(NumericFilter.Operator.LOWER_THAN);

            var phenylFilter = new NumericFilter();
            phenylFilter.setValue(List.of(new BigDecimal("50.0")));
            phenylFilter.setOperator(NumericFilter.Operator.GREATER_THAN);

            var filter = IngredientFilter.builder()
                    .fatContentFilter(fatFilter)
                    .proteinContentFilter(proteinFilter)
                    .carbsContentFilter(carbsFilter)
                    .phenylalanineContentFilter(phenylFilter)
                    .build();

            // when
            Specification<Ingredient> result = filter.toSpecification();

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should detect all active nutrition filters")
        void shouldDetectAllActiveNutritionFilters() {
            // given
            var fatFilter = new NumericFilter();
            fatFilter.setValue(List.of(new BigDecimal("1.0")));

            var proteinFilter = new NumericFilter();
            proteinFilter.setValue(List.of(new BigDecimal("5.0")));

            var carbsFilter = new NumericFilter();
            carbsFilter.setValue(List.of(new BigDecimal("20.0")));

            var phenylFilter = new NumericFilter();
            phenylFilter.setValue(List.of(new BigDecimal("50.0")));

            var filter = IngredientFilter.builder()
                    .fatContentFilter(fatFilter)
                    .proteinContentFilter(proteinFilter)
                    .carbsContentFilter(carbsFilter)
                    .phenylalanineContentFilter(phenylFilter)
                    .build();

            // then
            assertThat(filter.isFatContentFilterActive()).isTrue();
            assertThat(filter.isProteinContentFilterActive()).isTrue();
            assertThat(filter.isCarbsContentFilterActive()).isTrue();
            assertThat(filter.isPhenylalanineContentFilterActive()).isTrue();
        }
    }
}
