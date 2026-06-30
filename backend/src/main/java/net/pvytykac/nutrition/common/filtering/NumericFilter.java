package net.pvytykac.nutrition.common.filtering;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;

/**
 * Filter for numeric fields with various comparison operators.
 */
@Getter
@Setter
public class NumericFilter {

    /**
     * Operators for numeric filtering.
     */
    public enum Operator {
        EQUAL,
        GREATER_THAN,
        LOWER_THAN,
        GREATER_THAN_OR_EQUAL,
        LOWER_THAN_OR_EQUAL,
        BETWEEN
    }

    private @Nullable Collection<BigDecimal> value;
    private Operator operator = Operator.GREATER_THAN;

    /**
     * Checks if this filter is active (has values to filter by).
     *
     * @return true if value is not null and not empty
     */
    public boolean isActive() {
        return value != null && !value.isEmpty();
    }

    /**
     * Returns the minimum value in the collection.
     * Used for LOWER_THAN and LOWER_THAN_OR_EQUAL operators.
     *
     * @return minimum value
     */
    public @Nullable BigDecimal getMinValue() {
        if (!isActive()) {
            return null;
        }
        return Collections.min(value);
    }

    /**
     * Returns the maximum value in the collection.
     * Used for GREATER_THAN and GREATER_THAN_OR_EQUAL operators.
     *
     * @return maximum value
     */
    public @Nullable BigDecimal getMaxValue() {
        if (!isActive()) {
            return null;
        }
        return Collections.max(value);
    }

    /**
     * Returns a single value from the collection (first value).
     * Used for EQUAL operator.
     *
     * @return first value in the collection
     */
    public @Nullable BigDecimal getSingleValue() {
        var localValue = value;
        if (localValue == null || localValue.isEmpty()) {
            return null;
        }
        return localValue.iterator().next();
    }
}
