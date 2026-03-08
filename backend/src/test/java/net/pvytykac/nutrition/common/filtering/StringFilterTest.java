package net.pvytykac.nutrition.common.filtering;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StringFilter")
class StringFilterTest {

    @Nested
    @DisplayName("default values")
    class DefaultValues {

        @Test
        @DisplayName("should have STARTS_WITH as default operator")
        void shouldHaveStartsWithAsDefaultOperator() {
            // given
            var filter = new StringFilter();

            // then
            assertThat(filter.getOperator()).isEqualTo(StringFilter.Operator.STARTS_WITH);
        }

        @Test
        @DisplayName("should have null value by default")
        void shouldHaveNullValueByDefault() {
            // given
            var filter = new StringFilter();

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
            var filter = new StringFilter();
            filter.setValue(null);

            // then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return false when value is empty")
        void shouldReturnFalseWhenValueIsEmpty() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of());

            // then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return true when value is not empty")
        void shouldReturnTrueWhenValueIsNotEmpty() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of("test"));

            // then
            assertThat(filter.isActive()).isTrue();
        }

        @Test
        @DisplayName("should return true with multiple values")
        void shouldReturnTrueWithMultipleValues() {
            // given
            var filter = new StringFilter();
            filter.setValue(List.of("value1", "value2"));

            // then
            assertThat(filter.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("operators")
    class Operators {

        @Test
        @DisplayName("should support EXACT_MATCH operator")
        void shouldSupportExactMatch() {
            // given
            var filter = new StringFilter();
            filter.setOperator(StringFilter.Operator.EXACT_MATCH);

            // then
            assertThat(filter.getOperator()).isEqualTo(StringFilter.Operator.EXACT_MATCH);
        }

        @Test
        @DisplayName("should support STARTS_WITH operator")
        void shouldSupportStartsWith() {
            // given
            var filter = new StringFilter();
            filter.setOperator(StringFilter.Operator.STARTS_WITH);

            // then
            assertThat(filter.getOperator()).isEqualTo(StringFilter.Operator.STARTS_WITH);
        }

        @Test
        @DisplayName("should support ENDS_WITH operator")
        void shouldSupportEndsWith() {
            // given
            var filter = new StringFilter();
            filter.setOperator(StringFilter.Operator.ENDS_WITH);

            // then
            assertThat(filter.getOperator()).isEqualTo(StringFilter.Operator.ENDS_WITH);
        }

        @Test
        @DisplayName("should support CONTAINS operator")
        void shouldSupportContains() {
            // given
            var filter = new StringFilter();
            filter.setOperator(StringFilter.Operator.CONTAINS);

            // then
            assertThat(filter.getOperator()).isEqualTo(StringFilter.Operator.CONTAINS);
        }

        @Test
        @DisplayName("should support IN operator")
        void shouldSupportIn() {
            // given
            var filter = new StringFilter();
            filter.setOperator(StringFilter.Operator.IN);

            // then
            assertThat(filter.getOperator()).isEqualTo(StringFilter.Operator.IN);
        }
    }

    @Nested
    @DisplayName("getters and setters")
    class GettersAndSetters {

        @Test
        @DisplayName("should set and get value")
        void shouldSetAndGetValue() {
            // given
            var filter = new StringFilter();
            var values = List.of("test1", "test2");

            // when
            filter.setValue(values);

            // then
            assertThat(filter.getValue()).isEqualTo(values);
        }

        @Test
        @DisplayName("should set and get operator")
        void shouldSetAndGetOperator() {
            // given
            var filter = new StringFilter();

            // when
            filter.setOperator(StringFilter.Operator.CONTAINS);

            // then
            assertThat(filter.getOperator()).isEqualTo(StringFilter.Operator.CONTAINS);
        }
    }
}
