package net.pvytykac.nutrition.util.filtering;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
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

    private Collection<BigDecimal> value;
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
    public BigDecimal getMinValue() {
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
    public BigDecimal getMaxValue() {
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
    public BigDecimal getSingleValue() {
        if (!isActive()) {
            return null;
        }
        return value.iterator().next();
    }
}
