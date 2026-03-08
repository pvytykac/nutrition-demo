package net.pvytykac.nutrition.util.filtering;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.List;

/**
 * Builds JPA Specifications from filter objects.
 * Generic specification builder that works with any entity type.
 */
public final class SpecificationBuilder {

    private SpecificationBuilder() {}

    /**
     * Creates a specification for string filtering.
     *
     * @param filter the string filter
     * @param fieldExpression expression to access the field from root
     * @param <T> the entity type
     * @return Specification or null if filter is not active
     */
    public static <T> Specification<T> stringFilter(
            StringFilter filter,
            FieldExpression<T, String> fieldExpression) {

        if (!filter.isActive()) {
            return null;
        }

        List<String> values = List.copyOf(filter.getValue());
        StringFilter.Operator operator = filter.getOperator();

        return (root, _, cb) -> {
            Path<String> field = fieldExpression.apply(root);
            return applyStringOperator(cb, field, values, operator);
        };
    }

    /**
     * Creates a specification for numeric filtering.
     *
     * @param filter the numeric filter
     * @param fieldExpression expression to access the field from root
     * @param <T> the entity type
     * @return Specification or null if filter is not active
     */
    public static <T> Specification<T> numericFilter(
            NumericFilter filter,
            FieldExpression<T, BigDecimal> fieldExpression) {

        if (!filter.isActive()) {
            return null;
        }

        BigDecimal minValue;
        BigDecimal maxValue = null;
        NumericFilter.Operator operator = filter.getOperator();

        switch (operator) {
            case BETWEEN -> {
                minValue = filter.getMinValue();
                maxValue = filter.getMaxValue();
            }
            case GREATER_THAN, GREATER_THAN_OR_EQUAL -> minValue = filter.getMaxValue();
            case LOWER_THAN, LOWER_THAN_OR_EQUAL -> minValue = filter.getMinValue();
            case EQUAL -> minValue = filter.getSingleValue();
            default -> throw new IllegalStateException("No case defined for operator");
        }

        final BigDecimal finalMinValue = minValue;
        final BigDecimal finalMaxValue = maxValue;

        return (root, _, cb) -> {
            Path<BigDecimal> field = fieldExpression.apply(root);
            return applyNumericOperator(cb, field, finalMinValue, finalMaxValue, operator);
        };
    }

    /**
     * Creates a specification for enum filtering.
     *
     * @param filter the enum filter
     * @param fieldExpression expression to access the field from root
     * @param <T> the entity type
     * @param <E> the enum type
     * @return Specification or null if filter is not active
     */
    public static <T, E extends Enum<E>, F extends EnumFilter<E>> Specification<T> enumFilter(
            F filter,
            FieldExpression<T, E> fieldExpression) {

        if (!filter.isActive()) {
            return null;
        }

        List<E> values = List.copyOf(filter.getValue());
        EnumFilter.Operator operator = filter.getOperator();

        return (root, _, cb) -> {
            Path<E> field = fieldExpression.apply(root);
            return applyEnumOperator(cb, field, values, operator);
        };
    }

    /**
     * Combines multiple specifications using AND logic.
     *
     * @param specs the list of specifications to combine
     * @param <T> the entity type
     * @return combined Specification or null if specs is empty
     */
    @SafeVarargs
    public static <T> Specification<T> combine(Specification<T>... specs) {
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

    private static Predicate applyStringOperator(
            CriteriaBuilder cb, Path<String> field, List<String> values, StringFilter.Operator operator) {
        List<String> lowerValues = values.stream()
                .map(String::toLowerCase)
                .toList();
        return switch (operator) {
            case EXACT_MATCH -> cb.equal(cb.lower(field), lowerValues.getFirst());
            case STARTS_WITH -> cb.like(cb.lower(field), lowerValues.getFirst() + "%");
            case ENDS_WITH -> cb.like(cb.lower(field), "%" + lowerValues.getFirst());
            case CONTAINS -> cb.like(cb.lower(field), "%" + lowerValues.getFirst() + "%");
            case IN -> cb.lower(field).in(lowerValues);
            default -> throw new IllegalStateException("No case defined for operator");
        };
    }

    private static Predicate applyNumericOperator(
            CriteriaBuilder cb, Path<BigDecimal> field, BigDecimal minValue,
            BigDecimal maxValue, NumericFilter.Operator operator) {
        return switch (operator) {
            case EQUAL -> cb.equal(field, minValue);
            case GREATER_THAN -> cb.greaterThan(field, minValue);
            case GREATER_THAN_OR_EQUAL -> cb.greaterThanOrEqualTo(field, minValue);
            case LOWER_THAN -> cb.lessThan(field, minValue);
            case LOWER_THAN_OR_EQUAL -> cb.lessThanOrEqualTo(field, minValue);
            case BETWEEN -> cb.between(field, minValue, maxValue != null ? maxValue : minValue);
            default -> throw new IllegalStateException("No case defined for operator");
        };
    }

    private static <E extends Enum<E>> Predicate applyEnumOperator(
            CriteriaBuilder cb, Path<E> field, List<E> values, EnumFilter.Operator operator) {
        return switch (operator) {
            case IN -> field.in(values);
            case NOT_IN -> cb.not(field.in(values));
        };
    }

    /**
     * Functional interface for extracting field paths from entity root.
     */
    @FunctionalInterface
    public interface FieldExpression<T, Y> {
        Path<Y> apply(Root<T> root);
    }
}
