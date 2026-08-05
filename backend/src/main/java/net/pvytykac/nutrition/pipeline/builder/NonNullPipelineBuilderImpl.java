package net.pvytykac.nutrition.pipeline.builder;

import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.pipeline.Pipeline;
import net.pvytykac.nutrition.pipeline.stage.filter.PipelineFilter;
import net.pvytykac.nutrition.pipeline.stage.transform.PipelineTransformation;

import java.util.Optional;
import java.util.function.Function;

import static net.pvytykac.nutrition.pipeline.builder.PipelineBuilderAdapter.filterFunction;
import static net.pvytykac.nutrition.pipeline.builder.PipelineBuilderAdapter.transformFunction;

/**
 * @author Paly
 * @since 2026-08-04
 */
@RequiredArgsConstructor
final class NonNullPipelineBuilderImpl<I, C> implements PipelineBuilder.NonNullPipelineBuilder<I, C> {

    private final Function<I, C> function;

    @Override
    public OptionalPipelineBuilder<I, C> filter(PipelineFilter<C> filter) {
        var filterFunction = filterFunction(filter);
        return new OptionalPipelineBuilderImpl<>(this.function.andThen(filterFunction));
    }

    @Override
    public <N> NonNullPipelineBuilder<I, N> transform(PipelineTransformation<C, N> transformation) {
        var transformFunction = transformFunction(transformation);
        return new NonNullPipelineBuilderImpl<>(this.function.andThen(transformFunction));
    }

    @Override
    public <N> OptionalPipelineBuilder<I, N> flatTransform(PipelineTransformation<C, Optional<N>> transformation) {
        var transformFunction = transformFunction(transformation);
        return new OptionalPipelineBuilderImpl<>(this.function.andThen(transformFunction));
    }

    @Override
    public <O> LookupPipelineBuilder<I, C, O> lookup(Function<InitialPipelineBuilder<C>, Pipeline<C, Optional<O>>> lookup) {
        return LookupPipelineBuilderImpl.of(function.andThen(Optional::of), lookup.apply(new InitialPipelineBuilderImpl<>())
                .toFunction());
    }

    @Override
    public Pipeline<I, C> build() {
        return Pipeline.untraced(function);
    }
}
