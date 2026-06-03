package net.pvytykac.nutrition.common.filtering;

import net.pvytykac.nutrition.common.filtering.TestEntity.TestStatus;
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
            EnumFilter<TestStatus> filter = new EnumFilter<>();

            // then
            assertThat(filter.getOperator()).isEqualTo(EnumFilter.Operator.IN);
        }

        @Test
        @DisplayName("should have null value by default")
        void shouldHaveNullValueByDefault() {
            // given
            EnumFilter<TestStatus> filter = new EnumFilter<>();

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
            EnumFilter<TestStatus> filter = new EnumFilter<>();
            filter.setValue(null);

            // then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return false when value is empty")
        void shouldReturnFalseWhenValueIsEmpty() {
            // given
            EnumFilter<TestStatus> filter = new EnumFilter<>();
            filter.setValue(List.of());

            // then
            assertThat(filter.isActive()).isFalse();
        }

        @Test
        @DisplayName("should return true when value has elements")
        void shouldReturnTrueWhenValueHasElements() {
            // given
            EnumFilter<TestStatus> filter = new EnumFilter<>();
            filter.setValue(List.of(TestStatus.ACTIVE));

            // then
            assertThat(filter.isActive()).isTrue();
        }

        @Test
        @DisplayName("should return true with multiple values")
        void shouldReturnTrueWithMultipleValues() {
            // given
            EnumFilter<TestStatus> filter = new EnumFilter<>();
            filter.setValue(List.of(TestStatus.ACTIVE, TestStatus.DELETED));

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
            EnumFilter<TestStatus> filter = new EnumFilter<>();
            filter.setOperator(EnumFilter.Operator.IN);

            // then
            assertThat(filter.getOperator()).isEqualTo(EnumFilter.Operator.IN);
        }

        @Test
        @DisplayName("should support NOT_IN operator")
        void shouldSupportNotIn() {
            // given
            EnumFilter<TestStatus> filter = new EnumFilter<>();
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
            EnumFilter<TestStatus> filter = new EnumFilter<>();
            filter.setValue(List.of(TestStatus.ACTIVE, TestStatus.DELETED));

            // then
            assertThat(filter.isActive()).isTrue();
            assertThat(filter.getValue()).containsExactly(TestStatus.ACTIVE, TestStatus.DELETED);
        }
    }


}
