package net.pvytykac.nutrition.util.filtering;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Generic filter builder that can create JPA Specifications for any entity type.
 * 
 * @param <T> the entity type to filter
 */
public final class FilterBuilder<T> {

    private FilterBuilder() {}

    /**
     * Creates a specification that filters by a string field.
     *
     * @param rootExtractor function to extract the field path from the root
     * @param value the value to filter by
     * @param operator the string operator to apply
     * @param <T> the entity type
     * @return a Specification or null if value is null/blank
     */
    public static <T> Specification<T> stringFilter(
            Function<Root<T>, Path<String>> rootExtractor,
            String value, 
            StringOperator operator) {
        
        if (value == null || value.isBlank()) {
            return null;
        }

        return (root, query, cb) -> {
            Path<String> field = rootExtractor.apply(root);
            return applyStringOperator(cb, field, value, operator);
        };
    }

    /**
     * Creates a specification that filters by a comparable/number field.
     *
     * @param rootExtractor function to extract the field path from the root
     * @param value the value to filter by
     * @param secondValue second value for BETWEEN operator
     * @param operator the number operator to apply
     * @param <Y> the comparable type
     * @param <T> the entity type
     * @return a Specification or null if value is null
     */
    @SuppressWarnings("unchecked")
    public static <Y extends Comparable<Y>, T> Specification<T> comparableFilter(
            Function<Root<T>, Path<Y>> rootExtractor,
            Y value, 
            Y secondValue,
            NumberOperator operator) {
        
        if (value == null) {
            return null;
        }

        return (root, query, cb) -> {
            Path<Y> field = rootExtractor.apply(root);
            return applyComparableOperator(cb, field, value, secondValue, operator);
        };
    }

    /**
     * Creates a specification that filters by an enum field.
     *
     * @param rootExtractor function to extract the field path from the root
     * @param values the set of enum values to filter by
     * @param operator the enum operator to apply (IN or NOT_IN)
     * @param <Y> the enum type
     * @param <T> the entity type
     * @return a Specification or null if values is null/empty
     */
    @SuppressWarnings("unchecked")
    public static <Y extends Enum<Y>, T> Specification<T> enumFilter(
            Function<Root<T>, Path<Y>> rootExtractor,
            List<Y> values, 
            EnumOperator operator) {
        
        if (values == null || values.isEmpty()) {
            return null;
        }

        return (root, query, cb) -> {
            Path<Y> field = rootExtractor.apply(root);
            return applyEnumOperator(cb, field, values, operator);
        };
    }

    /**
     * Combines multiple specifications using AND logic.
     *
     * @param specs the list of specifications to combine
     * @param <T> the entity type
     * @return a combined Specification or null if specs is empty
     */
    public static <T> Specification<T> combine(List<Specification<T>> specs) {
        if (specs == null || specs.isEmpty()) {
            return null;
        }
        
        Specification<T> combined = null;
        for (Specification<T> spec : specs) {
            if (spec != null) {
                if (combined == null) {
                    combined = spec;
                } else {
                    combined = combined.and(spec);
                }
            }
        }
        return combined;
    }

    private static <T> Predicate applyStringOperator(CriteriaBuilder cb, Path<String> field, String value, StringOperator operator) {
        String lowerValue = value.toLowerCase();
        return switch (operator) {
            case EQUALS -> cb.equal(cb.lower(field), lowerValue);
            case STARTS_WITH -> cb.like(cb.lower(field), lowerValue + "%");
            case ENDS_WITH -> cb.like(cb.lower(field), "%" + lowerValue);
            case CONTAINS -> cb.like(cb.lower(field), "%" + lowerValue + "%");
        };
    }

    @SuppressWarnings("unchecked")
    private static <Y extends Comparable<Y>, T> Predicate applyComparableOperator(
            CriteriaBuilder cb, Path<Y> field, Y value, Y secondValue, NumberOperator operator) {
        
        // For BigDecimal, we need special handling
        if (value instanceof BigDecimal) {
            return applyBigDecimalOperator(cb, (Path<BigDecimal>) field, (BigDecimal) value, (BigDecimal) secondValue, operator);
        }
        
        return switch (operator) {
            case EQUALS -> cb.equal(field, value);
            case GREATER_THAN -> cb.greaterThan(field, value);
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(field, value);
            case LOWER_THAN -> cb.lessThan(field, value);
            case LOWER_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(field, value);
            case BETWEEN -> cb.between(field, value, secondValue != null ? secondValue : value);
        };
    }

    private static <T> Predicate applyBigDecimalOperator(
            CriteriaBuilder cb, Path<BigDecimal> field, BigDecimal value, BigDecimal secondValue, NumberOperator operator) {
        
        return switch (operator) {
            case EQUALS -> cb.equal(field, value);
            case GREATER_THAN -> cb.greaterThan(field, value);
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(field, value);
            case LOWER_THAN -> cb.lessThan(field, value);
            case LOWER_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(field, value);
            case BETWEEN -> cb.between(field, value, secondValue != null ? secondValue : value);
        };
    }

    @SuppressWarnings("unchecked")
    private static <Y extends Enum<Y>, T> Predicate applyEnumOperator(
            CriteriaBuilder cb, Path<Y> field, List<Y> values, EnumOperator operator) {
        
        return switch (operator) {
            case IN -> field.in(values);
            case NOT_IN -> cb.not(field.in(values));
        };
    }
}
