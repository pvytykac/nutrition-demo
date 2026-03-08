package net.pvytykac.nutrition.common.filtering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("NumericFilter")
class NumericFilterTest {

    @Nested
    @DisplayName("default values")
    class DefaultValues {

        @Test
        @DisplayName("should have GREATER_THAN as default operator")
        void shouldHaveGreaterThanAsDefaultOperator() {
            // given
            var filter = new NumericFilter();

            // then
            assertThat(filter.getOperator()).isEqualTo(NumericFilter.Operator.GREATER_THAN);
        }

        @Test
        @DisplayName("should have null value by default")
        void shouldHaveNullValueByDefault() {
            // given
            var filter = new NumericFilter();

            // then
            assertThat(filter.getValue()).isNull();
        }
    }

    @Nested
    @DisplayName("isActive")
    class IsActive {

        @Test
        @DisplayName("should return false when value is null")
        void shouldReturnFalseWhenValueIsNull() {
            // given
            var filter = new NumericFilter();
            filter.setValue(null);

            // then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return false when value is empty")
        void shouldReturnFalseWhenValueIsEmpty() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of());

            // then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return true when value has one element")
        void shouldReturnTrueWhenValueHasOneElement() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0")));

            // then
            assertThat(filter.isActive()).isTrue();
        }

        @Test
        @DisplayName("should return true when value has multiple elements")
        void shouldReturnTrueWithMultipleValues() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0"), new BigDecimal("20.0")));

            // then
            assertThat(filter.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("operators")
    class Operators {

        @Test
        @DisplayName("should support EQUAL operator")
        void shouldSupportEqual() {
            // given
            var filter = new NumericFilter();
            filter.setOperator(NumericFilter.Operator.EQUAL);

            // then
            assertThat(filter.getOperator()).isEqualTo(NumericFilter.Operator.EQUAL);
        }

        @Test
        @DisplayName("should support GREATER_THAN operator")
        void shouldSupportGreaterThan() {
            // given
            var filter = new NumericFilter();
            filter.setOperator(NumericFilter.Operator.GREATER_THAN);

            // then
            assertThat(filter.getOperator()).isEqualTo(NumericFilter.Operator.GREATER_THAN);
        }

        @Test
        @DisplayName("should support LOWER_THAN operator")
        void shouldSupportLowerThan() {
            // given
            var filter = new NumericFilter();
            filter.setOperator(NumericFilter.Operator.LOWER_THAN);

            // then
            assertThat(filter.getOperator()).isEqualTo(NumericFilter.Operator.LOWER_THAN);
        }

        @Test
        @DisplayName("should support GREATER_THAN_OR_EQUAL operator")
        void shouldSupportGreaterThanOrEqual() {
            // given
            var filter = new NumericFilter();
            filter.setOperator(NumericFilter.Operator.GREATER_THAN_OR_EQUAL);

            // then
            assertThat(filter.getOperator()).isEqualTo(NumericFilter.Operator.GREATER_THAN_OR_EQUAL);
        }

        @Test
        @DisplayName("should support LOWER_THAN_OR_EQUAL operator")
        void shouldSupportLowerThanOrEqual() {
            // given
            var filter = new NumericFilter();
            filter.setOperator(NumericFilter.Operator.LOWER_THAN_OR_EQUAL);

            // then
            assertThat(filter.getOperator()).isEqualTo(NumericFilter.Operator.LOWER_THAN_OR_EQUAL);
        }

        @Test
        @DisplayName("should support BETWEEN operator")
        void shouldSupportBetween() {
            // given
            var filter = new NumericFilter();
            filter.setOperator(NumericFilter.Operator.BETWEEN);

            // then
            assertThat(filter.getOperator()).isEqualTo(NumericFilter.Operator.BETWEEN);
        }
    }

    @Nested
    @DisplayName("value extraction")
    class ValueExtraction {

        @Test
        @DisplayName("getMinValue should return minimum from collection")
        void shouldReturnMinimumValue() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("50.0"), new BigDecimal("10.0"), new BigDecimal("30.0")));

            // then
            assertThat(filter.getMinValue()).isEqualByComparingTo(new BigDecimal("10.0"));
        }

        @Test
        @DisplayName("getMinValue should return null when filter is inactive")
        void shouldReturnNullForMinWhenInactive() {
            // given
            var filter = new NumericFilter();
            filter.setValue(null);

            // then
            assertThat(filter.getMinValue()).isNull();
        }

        @Test
        @DisplayName("getMaxValue should return maximum from collection")
        void shouldReturnMaximumValue() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("10.0"), new BigDecimal("50.0"), new BigDecimal("30.0")));

            // then
            assertThat(filter.getMaxValue()).isEqualByComparingTo(new BigDecimal("50.0"));
        }

        @Test
        @DisplayName("getMaxValue should return null when filter is inactive")
        void shouldReturnNullForMaxWhenInactive() {
            // given
            var filter = new NumericFilter();
            filter.setValue(null);

            // then
            assertThat(filter.getMaxValue()).isNull();
        }

        @Test
        @DisplayName("getSingleValue should return first element")
        void shouldReturnFirstElement() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("25.0"), new BigDecimal("50.0")));

            // then
            assertThat(filter.getSingleValue()).isEqualByComparingTo(new BigDecimal("25.0"));
        }

        @Test
        @DisplayName("getSingleValue should return null when filter is inactive")
        void shouldReturnNullForSingleWhenInactive() {
            // given
            var filter = new NumericFilter();
            filter.setValue(null);

            // then
            assertThat(filter.getSingleValue()).isNull();
        }

        @Test
        @DisplayName("should handle single value in collection")
        void shouldHandleSingleValue() {
            // given
            var filter = new NumericFilter();
            filter.setValue(List.of(new BigDecimal("42.0")));

            // then
            assertThat(filter.getMinValue()).isEqualByComparingTo(new BigDecimal("42.0"));
            assertThat(filter.getMaxValue()).isEqualByComparingTo(new BigDecimal("42.0"));
            assertThat(filter.getSingleValue()).isEqualByComparingTo(new BigDecimal("42.0"));
        }
    }
}
