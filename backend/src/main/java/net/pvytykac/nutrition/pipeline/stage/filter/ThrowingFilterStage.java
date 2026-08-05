package net.pvytykac.nutrition.pipeline.stage.filter;

import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.pipeline.Pipeline;
import net.pvytykac.nutrition.pipeline.stage.Stage;

import java.security.SecureRandom;
import java.util.Map;

/**
 * @author Paly
 * @since 2026-08-04
 */
@RequiredArgsConstructor
public class ThrowingFilterStage<T> implements Stage.FilterStage<T> {

    private final SecureRandom random = new SecureRandom();

    @Override
    public String getId() {
        return this.getClass().getSimpleName();
    }

    @Override
    public boolean matches(T input) {
        if (random.nextDouble() >= 0.1D) {
            Pipeline.getContextBuilder()
                    .debugInfo(this, Map.of("input", input, "output", true));
            return true;
        }

        var ex = new RuntimeException("Something Went Wrong");

        Pipeline.getContextBuilder()
                .debugInfo(this, Map.of("input", input, "error", ex));

        throw ex;
    }

}
