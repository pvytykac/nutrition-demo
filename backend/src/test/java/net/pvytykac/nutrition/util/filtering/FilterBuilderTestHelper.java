package net.pvytykac.nutrition.util.filtering;

import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

/**
 * Test helper that provides concrete type bindings for FilterBuilder.
 * This avoids Java generic type inference issues in tests.
 */
public class FilterBuilderTestHelper {

    public static class TestEntity {
        public String name;
        public BigDecimal count;
    }

    public enum TestStatus {
        ACTIVE, INACTIVE
    }

    // String filter methods with concrete types
    public static Specification<TestEntity> filterByName(String value, StringOperator operator) {
        return FilterBuilder.stringFilter(root -> root.get("name"), value, operator);
    }

    public static Specification<TestEntity> filterByCount(BigDecimal value, BigDecimal secondValue, NumberOperator operator) {
        return FilterBuilder.comparableFilter(root -> root.get("count"), value, secondValue, operator);
    }

    public static Specification<TestEntity> filterByStatus(List<TestStatus> values, EnumOperator operator) {
        return FilterBuilder.enumFilter(root -> root.get("status"), values, operator);
    }

    public static Specification<TestEntity> combineSpecs(List<Specification<TestEntity>> specs) {
        return FilterBuilder.combine(specs);
    }
}
