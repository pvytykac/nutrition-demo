package net.pvytykac.nutrition.pipeline.builder;

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
final class InitialPipelineBuilderImpl<I> implements PipelineBuilder.InitialPipelineBuilder<I> {

    @Override
    public OptionalPipelineBuilder<I, I> filter(PipelineFilter<I> filter) {
        var function = filterFunction(filter);
        return new OptionalPipelineBuilderImpl<>(function);
    }

    @Override
    public <N> NonNullPipelineBuilder<I, N> transform(PipelineTransformation<I, N> transformation) {
        var function = transformFunction(transformation);
        return new NonNullPipelineBuilderImpl<>(function);
    }

    @Override
    public <N> OptionalPipelineBuilder<I, N> flatTransform(PipelineTransformation<I, Optional<N>> transformation) {
        var function = transformFunction(transformation);
        return new OptionalPipelineBuilderImpl<>(function);
    }

    @Override
    public <O> LookupPipelineBuilder<I, I, O> lookup(Function<InitialPipelineBuilder<I>, Pipeline<I, Optional<O>>> lookup) {
        return LookupPipelineBuilderImpl.of(Optional::of, lookup.apply(new InitialPipelineBuilderImpl<>()).toFunction());
    }
}
