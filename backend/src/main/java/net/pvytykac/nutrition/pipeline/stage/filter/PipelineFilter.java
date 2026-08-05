package net.pvytykac.nutrition.pipeline.stage.filter;

import java.util.stream.Stream;

public interface PipelineFilter<I> {

    boolean matches(I input);

    default PipelineFilter<I> or(PipelineFilter<I> other) {
        return input -> Stream.of(this, other).anyMatch(f -> f.matches(input));
    }

    default PipelineFilter<I> and(PipelineFilter<I> other) {
        return input -> Stream.of(this, other).allMatch(f -> f.matches(input));
    }
}