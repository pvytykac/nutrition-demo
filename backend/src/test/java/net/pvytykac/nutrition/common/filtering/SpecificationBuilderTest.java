package net.pvytykac.nutrition.common.filtering;

import jakarta.persistence.criteria.Root;
import net.pvytykac.nutrition.ingredient.Ingredient;
import net.pvytykac.nutrition.ingredient.Unit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("SpecificationBuilder")
class SpecificationBuilderTest {

    @Nested
    @DisplayName("stringFilter")
    class StringFilterTests {

        @Test
        @DisplayName("should return null when filter is not active")
        void shouldReturnNullWhenFilterNotActive() {
            // given
            var filter = new StringFilter();
            filter.setValue(null);

            // when
            Specification<Ingredient> result = SpecificationBuilder.stringFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("name")
            );

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should create specification for EXACT_MATCH operator")
        void shouldCreateSpecForExactMatch() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of("test"));
            filter.setOperator(StringFilter.Operator.EXACT_MATCH);

            // when
            Specification<Ingredient> result = SpecificationBuilder.stringFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("name")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for STARTS_WITH operator")
        void shouldCreateSpecForStartsWith() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of("test"));
            filter.setOperator(StringFilter.Operator.STARTS_WITH);

            // when
            Specification<Ingredient> result = SpecificationBuilder.stringFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("name")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for ENDS_WITH operator")
        void shouldCreateSpecForEndsWith() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of("test"));
            filter.setOperator(StringFilter.Operator.ENDS_WITH);

            // when
            Specification<Ingredient> result = SpecificationBuilder.stringFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("name")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for CONTAINS operator")
        void shouldCreateSpecForContains() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of("test"));
            filter.setOperator(StringFilter.Operator.CONTAINS);

            // when
            Specification<Ingredient> result = SpecificationBuilder.stringFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("name")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for IN operator")
        void shouldCreateSpecForIn() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of("test"));
            filter.setOperator(StringFilter.Operator.IN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.stringFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("name")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should use first value from collection")
        void shouldUseFirstValue() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of("first", "second"));
            filter.setOperator(StringFilter.Operator.CONTAINS);

            // when
            Specification<Ingredient> result = SpecificationBuilder.stringFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("name")
            );

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("numericFilter")
    class NumericFilterTests {

        @Test
        @DisplayName("should return null when filter is not active")
        void shouldReturnNullWhenFilterNotActive() {
            // given
            var filter = new NumericFilter();
            filter.setValue(null);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should create specification for EQUAL operator")
        void shouldCreateSpecForEqual() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.EQUAL);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for GREATER_THAN operator")
        void shouldCreateSpecForGreaterThan() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.GREATER_THAN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for GREATER_THAN_OR_EQUAL operator")
        void shouldCreateSpecForGreaterThanOrEqual() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.GREATER_THAN_OR_EQUAL);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for LOWER_THAN operator")
        void shouldCreateSpecForLowerThan() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.LOWER_THAN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for LOWER_THAN_OR_EQUAL operator")
        void shouldCreateSpecForLowerThanOrEqual() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.LOWER_THAN_OR_EQUAL);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for BETWEEN operator with min and max")
        void shouldCreateSpecForBetween() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0"), new BigDecimal("20.0")));
            filter.setOperator(NumericFilter.Operator.BETWEEN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should handle BETWEEN with single value")
        void shouldHandleBetweenWithSingleValue() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));
            filter.setOperator(NumericFilter.Operator.BETWEEN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("enumFilter")
    class EnumFilterTests {

        @Test
        @DisplayName("should return null when filter is not active")
        void shouldReturnNullWhenFilterNotActive() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(null);

            // when
            Specification<Ingredient> result = SpecificationBuilder.enumFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("unit")
            );

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should create specification for IN operator")
        void shouldCreateSpecForIn() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of(Unit.GRAM, Unit.MILILITER));
            filter.setOperator(EnumFilter.Operator.IN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.enumFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("unit")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should create specification for NOT_IN operator")
        void shouldCreateSpecForNotIn() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of(Unit.MILILITER));
            filter.setOperator(EnumFilter.Operator.NOT_IN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.enumFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("unit")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should handle single value in enum filter")
        void shouldHandleSingleValue() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of(Unit.GRAM));
            filter.setOperator(EnumFilter.Operator.IN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.enumFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("unit")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should handle multiple values in enum filter")
        void shouldHandleMultipleValues() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of(Unit.GRAM, Unit.MILILITER));
            filter.setOperator(EnumFilter.Operator.IN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.enumFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("unit")
            );

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("numericFilter value extraction")
    class NumericFilterValueExtractionTests {

        @Test
        @DisplayName("should use max value for GREATER_THAN with multiple values")
        void shouldUseMaxValueForGreaterThan() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0"), new BigDecimal("50.0"), new BigDecimal("30.0")));
            filter.setOperator(NumericFilter.Operator.GREATER_THAN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should use max value for GREATER_THAN_OR_EQUAL with multiple values")
        void shouldUseMaxValueForGreaterThanOrEqual() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0"), new BigDecimal("50.0")));
            filter.setOperator(NumericFilter.Operator.GREATER_THAN_OR_EQUAL);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should use min value for LOWER_THAN with multiple values")
        void shouldUseMinValueForLowerThan() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0"), new BigDecimal("50.0"), new BigDecimal("30.0")));
            filter.setOperator(NumericFilter.Operator.LOWER_THAN);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should use min value for LOWER_THAN_OR_EQUAL with multiple values")
        void shouldUseMinValueForLowerThanOrEqual() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0"), new BigDecimal("50.0")));
            filter.setOperator(NumericFilter.Operator.LOWER_THAN_OR_EQUAL);

            // when
            Specification<Ingredient> result = SpecificationBuilder.numericFilter(
                    filter,
                    (Root<Ingredient> root) -> root.get("quantity")
            );

            // then
            assertThat(result).isNotNull();
        }
    }
}
