package net.pvytykac.nutrition.pipeline;

import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.function.Function;

/**
 * @author Paly
 * @since 2026-08-04
 */
@RequiredArgsConstructor
class PipelineImpl<I, O> implements Pipeline<I, O> {

    private final Function<I, O> function;

    @Override
    public Output<O> execute(I input) {
        return new Output<>(function.apply(input), new Context(Collections.emptyMap()));
    }

    @Override
    public Function<I, O> toFunction() {
        return function;
    }
}
