package net.pvytykac.nutrition.pipeline.stage.transform;

import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.pipeline.Pipeline;
import net.pvytykac.nutrition.pipeline.stage.Stage;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

/**
 * @author Paly
 * @since 2026-08-05
 */
@RequiredArgsConstructor
public class RollAndReturnOnMatchTransformStage<T> implements Stage.TransformStage<T, Optional<Integer>> {

    private final Integer number;
    private final SecureRandom random = new SecureRandom();

    @Override
    public String getId() {
        return this.getClass().getSimpleName() + "#" + this.number;
    }

    @Override
    public Optional<Integer> apply(T input) {
        var roll = random.nextInt(number) + 1;
        var result = roll == number ? Optional.of(number) : Optional.<Integer>empty();

        Pipeline.getContextBuilder()
                .debugInfo(this, Map.of("input", input, "roll", roll));

        return result;
    }
}
