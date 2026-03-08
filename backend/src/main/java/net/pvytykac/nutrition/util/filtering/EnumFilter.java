package net.pvytykac.nutrition.util.filtering;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * Filter for enum fields with IN/NOT_IN operators.
 *
 * @param <T> the enum type
 */
@Getter
@Setter
public class EnumFilter<T extends Enum<T>> {

    /**
     * Operators for enum filtering.
     */
    public enum Operator {
        IN,
        NOT_IN
    }

    private Collection<T> value;
    private Operator operator = Operator.IN;

    /**
     * Checks if this filter is active (has values to filter by).
     *
     * @return true if value is not null and not empty
     */
    public boolean isActive() {
        return value != null && !value.isEmpty();
    }
}
