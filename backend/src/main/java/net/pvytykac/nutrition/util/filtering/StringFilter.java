package net.pvytykac.nutrition.util.filtering;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * Filter for string fields with various matching operators.
 */
@Getter
@Setter
public class StringFilter {

    /**
     * Operators for string filtering.
     */
    public enum Operator {
        EXACT_MATCH,
        STARTS_WITH,
        ENDS_WITH,
        CONTAINS,
        IN
    }

    private Collection<String> value;
    private Operator operator = Operator.STARTS_WITH;

    /**
     * Checks if this filter is active (has values to filter by).
     *
     * @return true if value is not null and not empty
     */
    public boolean isActive() {
        return value != null && !value.isEmpty();
    }
}
