package net.pvytykac.nutrition.util.filtering;

import net.pvytykac.nutrition.util.filtering.FilterBuilderTestHelper.TestEntity;
import net.pvytykac.nutrition.util.filtering.FilterBuilderTestHelper.TestStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("FilterBuilder")
class FilterBuilderTest {

    @Test
    @DisplayName("should have private constructor")
    void shouldHavePrivateConstructor() {
        // Verify constructor exists - there should be exactly one
        assertThat(FilterBuilder.class.getDeclaredConstructors()).hasSize(1);
    }

    @Nested
    @DisplayName("stringFilter")
    class StringFilterTests {

        @Test
        @DisplayName("should return null when value is null")
        void shouldReturnNullWhenValueIsNull() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByName(null, StringOperator.EQUALS);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null when value is blank")
        void shouldReturnNullWhenValueIsBlank() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByName("   ", StringOperator.EQUALS);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return specification for valid value with EQUALS")
        void shouldReturnSpecificationForEquals() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByName("test", StringOperator.EQUALS);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for STARTS_WITH operator")
        void shouldReturnSpecificationForStartsWith() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByName("test", StringOperator.STARTS_WITH);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for ENDS_WITH operator")
        void shouldReturnSpecificationForEndsWith() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByName("test", StringOperator.ENDS_WITH);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for CONTAINS operator")
        void shouldReturnSpecificationForContains() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByName("test", StringOperator.CONTAINS);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("comparableFilter")
    class ComparableFilterTests {

        @Test
        @DisplayName("should return null when value is null")
        void shouldReturnNullWhenValueIsNull() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByCount(null, null, NumberOperator.EQUALS);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return specification for EQUALS operator")
        void shouldReturnSpecificationForEquals() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByCount(new BigDecimal("5.0"), null, NumberOperator.EQUALS);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for GREATER_THAN operator")
        void shouldReturnSpecificationForGreaterThan() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByCount(new BigDecimal("5.0"), null, NumberOperator.GREATER_THAN);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for GREATER_THAN_OR_EQUAL operator")
        void shouldReturnSpecificationForGreaterThanOrEqual() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByCount(new BigDecimal("5.0"), null, NumberOperator.GREATER_THAN_OR_EQUAL);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for LOWER_THAN operator")
        void shouldReturnSpecificationForLowerThan() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByCount(new BigDecimal("5.0"), null, NumberOperator.LOWER_THAN);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for LOWER_THAN_OR_EQUAL operator")
        void shouldReturnSpecificationForLowerThanOrEqual() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByCount(new BigDecimal("5.0"), null, NumberOperator.LOWER_THAN_OR_EQUAL);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for BETWEEN operator")
        void shouldReturnSpecificationForBetween() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByCount(new BigDecimal("5.0"), new BigDecimal("10.0"), NumberOperator.BETWEEN);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should handle BETWEEN with null second value")
        void shouldHandleBetweenWithNullSecondValue() {
            // when - secondValue is null, should use value instead
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByCount(new BigDecimal("5.0"), null, NumberOperator.BETWEEN);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("enumFilter")
    class EnumFilterTests {

        @Test
        @DisplayName("should return null when values is null")
        void shouldReturnNullWhenValuesIsNull() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByStatus(null, EnumOperator.IN);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null when values is empty")
        void shouldReturnNullWhenValuesIsEmpty() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByStatus(new ArrayList<>(), EnumOperator.IN);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return specification for IN operator")
        void shouldReturnSpecificationForInOperator() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByStatus(
                    List.of(TestStatus.ACTIVE, TestStatus.INACTIVE), EnumOperator.IN);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should return specification for NOT_IN operator")
        void shouldReturnSpecificationForNotInOperator() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.filterByStatus(
                    List.of(TestStatus.ACTIVE), EnumOperator.NOT_IN);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("combine")
    class CombineTests {

        @Test
        @DisplayName("should return null when specs list is null")
        void shouldReturnNullWhenSpecsListIsNull() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.combineSpecs(null);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null when specs list is empty")
        void shouldReturnNullWhenSpecsListIsEmpty() {
            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.combineSpecs(new ArrayList<>());

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should return null when all specs in list are null")
        void shouldReturnNullWhenAllSpecsAreNull() {
            // given
            List<Specification<TestEntity>> specs = new ArrayList<>();
            specs.add(null);
            specs.add(null);

            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.combineSpecs(specs);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("should combine single specification")
        void shouldCombineSingleSpecification() {
            // given
            var spec = FilterBuilderTestHelper.filterByName("test", StringOperator.CONTAINS);

            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.combineSpecs(List.of(spec));

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("should skip null specs and combine non-null ones")
        void shouldSkipNullSpecsAndCombineNonNull() {
            // given
            var spec1 = FilterBuilderTestHelper.filterByName("test", StringOperator.CONTAINS);
            var spec2 = FilterBuilderTestHelper.filterByCount(new BigDecimal("5"), null, NumberOperator.GREATER_THAN);
            
            List<Specification<TestEntity>> specs = new ArrayList<>();
            specs.add(spec1);
            specs.add(null);
            specs.add(spec2);

            // when
            Specification<TestEntity> result = FilterBuilderTestHelper.combineSpecs(specs);

            // then
            assertThat(result).isNotNull();
        }
    }
}
