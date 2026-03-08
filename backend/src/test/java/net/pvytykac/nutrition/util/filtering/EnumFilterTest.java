package net.pvytykac.nutrition.util.filtering;

import net.pvytykac.nutrition.ingredient.Unit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("EnumFilter")
class EnumFilterTest {

    @Nested
    @DisplayName("default values")
    class DefaultValues {

        @Test
        @DisplayName("should have IN as default operator")
        void shouldHaveInAsDefaultOperator() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();

            // then
            assertThat(filter.getOperator()).isEqualTo(EnumFilter.Operator.IN);
        }

        @Test
        @DisplayName("should have null value by default")
        void shouldHaveNullValueByDefault() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();

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
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(null);

            // then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return false when value is empty")
        void shouldReturnFalseWhenValueIsEmpty() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of());

            // then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return true when value has elements")
        void shouldReturnTrueWhenValueHasElements() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of(Unit.GRAM));

            // then
            assertThat(filter.isActive()).isTrue();
        }

        @Test
        @DisplayName("should return true with multiple values")
        void shouldReturnTrueWithMultipleValues() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setValue(List.of(Unit.GRAM, Unit.MILILITER));

            // then
            assertThat(filter.isActive()).isTrue();
        }
    }

    @Nested
    @DisplayName("operators")
    class Operators {

        @Test
        @DisplayName("should support IN operator")
        void shouldSupportIn() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setOperator(EnumFilter.Operator.IN);

            // then
            assertThat(filter.getOperator()).isEqualTo(EnumFilter.Operator.IN);
        }

        @Test
        @DisplayName("should support NOT_IN operator")
        void shouldSupportNotIn() {
            // given
            EnumFilter<Unit> filter = new EnumFilter<>();
            filter.setOperator(EnumFilter.Operator.NOT_IN);

            // then
            assertThat(filter.getOperator()).isEqualTo(EnumFilter.Operator.NOT_IN);
        }
    }

    @Nested
    @DisplayName("generics")
    class Generics {

        @Test
        @DisplayName("should work with different enum types")
        void shouldWorkWithDifferentEnumTypes() {
            // given
            EnumFilter<TestEnum> filter = new EnumFilter<>();
            filter.setValue(List.of(TestEnum.VALUE_A, TestEnum.VALUE_B));

            // then
            assertThat(filter.isActive()).isTrue();
            assertThat(filter.getValue()).containsExactly(TestEnum.VALUE_A, TestEnum.VALUE_B);
        }
    }

    private enum TestEnum {
        VALUE_A,
        VALUE_B
    }
}
