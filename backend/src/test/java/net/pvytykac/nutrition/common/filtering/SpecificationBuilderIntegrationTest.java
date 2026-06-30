package net.pvytykac.nutrition.common.filtering;

import net.pvytykac.nutrition.common.RepositoryTestBase;
import net.pvytykac.nutrition.common.filtering.TestEntity.TestStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NullAway")
@DisplayName("SpecificationBuilder Integration")
class SpecificationBuilderIntegrationTest extends RepositoryTestBase {

    @Autowired
    private TestRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Nested
    @DisplayName("stringFilter operators")
    class StringFilterOperators {

        @Test
        @DisplayName("should filter by EXACT_MATCH")
        void shouldFilterByExactMatch() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new StringFilter();
            filter.setValue(List.of("Apple"));
            filter.setOperator(StringFilter.Operator.EXACT_MATCH);

            Specification<TestEntity> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get(TestEntity_.NAME)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by STARTS_WITH")
        void shouldFilterByStartsWith() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new StringFilter();
            filter.setValue(List.of("App"));
            filter.setOperator(StringFilter.Operator.STARTS_WITH);

            Specification<TestEntity> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get(TestEntity_.NAME)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by ENDS_WITH")
        void shouldFilterByEndsWith() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new StringFilter();
            filter.setValue(List.of("le"));
            filter.setOperator(StringFilter.Operator.ENDS_WITH);

            Specification<TestEntity> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get(TestEntity_.NAME)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by CONTAINS")
        void shouldFilterByContains() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new StringFilter();
            filter.setValue(List.of("pp"));
            filter.setOperator(StringFilter.Operator.CONTAINS);

            Specification<TestEntity> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get(TestEntity_.NAME)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by IN")
        void shouldFilterByIn() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new StringFilter();
            filter.setValue(List.of("Apple"));
            filter.setOperator(StringFilter.Operator.IN);

            Specification<TestEntity> spec = SpecificationBuilder.stringFilter(
                    filter,
                    (root) -> root.get(TestEntity_.NAME)
            );

            // when
            var result = repository.findAll(spec);

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
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new NumericFilter();
            filter.setValue(List.of(BigDecimal.valueOf(100.0D)));
            filter.setOperator(NumericFilter.Operator.EQUAL);

            Specification<TestEntity> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get(TestEntity_.WEIGHT)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by GREATER_THAN")
        void shouldFilterByGreaterThan() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new NumericFilter();
            filter.setValue(List.of(BigDecimal.valueOf(15.0D)));
            filter.setOperator(NumericFilter.Operator.GREATER_THAN);

            Specification<TestEntity> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get(TestEntity_.WEIGHT)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by GREATER_THAN_OR_EQUAL")
        void shouldFilterByGreaterThanOrEqual() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new NumericFilter();
            filter.setValue(List.of(BigDecimal.valueOf(100.0D)));
            filter.setOperator(NumericFilter.Operator.GREATER_THAN_OR_EQUAL);

            Specification<TestEntity> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get(TestEntity_.WEIGHT)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Apple");
        }

        @Test
        @DisplayName("should filter by LOWER_THAN")
        void shouldFilterByLowerThan() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new NumericFilter();
            filter.setValue(List.of(BigDecimal.valueOf(15.0D)));
            filter.setOperator(NumericFilter.Operator.LOWER_THAN);

            Specification<TestEntity> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get(TestEntity_.WEIGHT)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Banana");
        }

        @Test
        @DisplayName("should filter by LOWER_THAN_OR_EQUAL")
        void shouldFilterByLowerThanOrEqual() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new NumericFilter();
            filter.setValue(List.of(BigDecimal.valueOf(10.0D)));
            filter.setOperator(NumericFilter.Operator.LOWER_THAN_OR_EQUAL);

            Specification<TestEntity> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get(TestEntity_.WEIGHT)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Banana");
        }

        @Test
        @DisplayName("should filter by BETWEEN")
        void shouldFilterByBetween() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(50.0), TestStatus.ACTIVE);
            saveTestEntity("Cantaloupe", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new NumericFilter();
            filter.setValue(List.of(BigDecimal.valueOf(15.0D), BigDecimal.valueOf(90.0D)));
            filter.setOperator(NumericFilter.Operator.BETWEEN);

            Specification<TestEntity> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get(TestEntity_.WEIGHT)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Banana");
        }

        @Test
        @DisplayName("should handle BETWEEN with null second value")
        void shouldHandleBetweenWithNullSecondValue() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            var filter = new NumericFilter();
            filter.setValue(List.of(BigDecimal.valueOf(10.0D)));
            filter.setOperator(NumericFilter.Operator.BETWEEN);

            Specification<TestEntity> spec = SpecificationBuilder.numericFilter(
                    filter,
                    (root) -> root.get(TestEntity_.WEIGHT)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getName()).isEqualTo("Banana");
        }
    }

    @Nested
    @DisplayName("enumFilter operators")
    class EnumFilterOperators {

        @Test
        @DisplayName("should filter by IN")
        void shouldFilterByIn() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            EnumFilter<TestStatus> filter = new EnumFilter<>();
            filter.setValue(List.of(TestStatus.DELETED));
            filter.setOperator(EnumFilter.Operator.IN);

            Specification<TestEntity> spec = SpecificationBuilder.enumFilter(
                    filter,
                    (root) -> root.get(TestEntity_.STATUS)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getStatus()).isEqualTo(TestStatus.DELETED);
        }

        @Test
        @DisplayName("should filter by NOT_IN")
        void shouldFilterByNotIn() {
            // given
            saveTestEntity("Apple", BigDecimal.valueOf(100.0), TestStatus.ACTIVE);
            saveTestEntity("Banana", BigDecimal.valueOf(10.0), TestStatus.DELETED);

            EnumFilter<TestStatus> filter = new EnumFilter<>();
            filter.setValue(List.of(TestStatus.DELETED));
            filter.setOperator(EnumFilter.Operator.NOT_IN);

            Specification<TestEntity> spec = SpecificationBuilder.enumFilter(
                    filter,
                    (root) -> root.get(TestEntity_.STATUS)
            );

            // when
            var result = repository.findAll(spec);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.getFirst().getStatus()).isNotEqualTo(TestStatus.DELETED);
        }
    }

    private TestEntity saveTestEntity(String name, BigDecimal weight, TestStatus status) {
        return testEntityManager.persistFlushFind(TestEntity.builder()
                .name(name)
                .weight(weight)
                .status(status)
                .build());
    }
}
