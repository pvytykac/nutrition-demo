package net.pvytykac.nutrition.pipeline.builder;

import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.pipeline.Pipeline;
import net.pvytykac.nutrition.pipeline.stage.filter.PipelineFilter;
import net.pvytykac.nutrition.pipeline.stage.transform.PipelineTransformation;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Paly
 * @since 2026-08-04
 */
@RequiredArgsConstructor
final class OptionalPipelineBuilderImpl<I, C> implements PipelineBuilder.OptionalPipelineBuilder<I, C> {

    private final Function<I, Optional<C>> function;

    @Override
    public OptionalPipelineBuilder<I, C> filter(PipelineFilter<C> filter) {
        var filterFunction = PipelineBuilderAdapter.optionalFilterFunction(filter);
        return new OptionalPipelineBuilderImpl<>(this.function.andThen(filterFunction));
    }

    @Override
    public <N> OptionalPipelineBuilder<I, N> transform(PipelineTransformation<C, N> transformation) {
        var transformFunction = PipelineBuilderAdapter.optionalTransformFunction(transformation);
        return new OptionalPipelineBuilderImpl<>(function.andThen(transformFunction));
    }

    @Override
    public <N> OptionalPipelineBuilder<I, N> flatTransform(PipelineTransformation<C, Optional<N>> transformation) {
        var transformFunction = PipelineBuilderAdapter.flatTransformFunction(transformation);
        return new OptionalPipelineBuilderImpl<>(function.andThen(transformFunction));
    }

    @Override
    public <O> LookupPipelineBuilder<I, C, O> lookup(Function<InitialPipelineBuilder<C>, Pipeline<C, Optional<O>>> lookup) {
        return LookupPipelineBuilderImpl.of(function, lookup.apply(new InitialPipelineBuilderImpl<>()).toFunction());
    }

    @Override
    public Pipeline<I, Optional<C>> build() {
        return Pipeline.untraced(this.function);
    }
}
