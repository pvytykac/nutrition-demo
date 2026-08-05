package net.pvytykac.nutrition.pipeline.builder;

import net.pvytykac.nutrition.pipeline.Pipeline;
import net.pvytykac.nutrition.pipeline.stage.filter.PipelineFilter;
import net.pvytykac.nutrition.pipeline.stage.transform.PipelineTransformation;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Paly
 * @since 2026-07-28
 */
public sealed interface PipelineBuilder permits PipelineBuilder.InitialPipelineBuilder, PipelineBuilder.BuildablePipelineBuilder {

    static <I> InitialPipelineBuilder<I> builder() {
        return new InitialPipelineBuilderImpl<>();
    }

    sealed interface InitialPipelineBuilder<I> extends PipelineBuilder permits InitialPipelineBuilderImpl {

        OptionalPipelineBuilder<I, I> filter(PipelineFilter<I> filter);

        <N> NonNullPipelineBuilder<I, N> transform(PipelineTransformation<I, N> transformation);

        <N> OptionalPipelineBuilder<I, N> flatTransform(PipelineTransformation<I, Optional<N>> transformation);

        <O> LookupPipelineBuilder<I, I, O> lookup(Function<InitialPipelineBuilder<I>, Pipeline<I, Optional<O>>> lookup);
    }

    sealed interface BuildablePipelineBuilder<I, C> extends PipelineBuilder permits NonNullPipelineBuilder, OptionalPipelineBuilder, LookupPipelineBuilder {
        Pipeline<I, C> build();
    }

    sealed interface NonNullPipelineBuilder<I, C> extends BuildablePipelineBuilder<I, C> permits NonNullPipelineBuilderImpl {
        OptionalPipelineBuilder<I, C> filter(PipelineFilter<C> filter);

        <N> NonNullPipelineBuilder<I, N> transform(PipelineTransformation<C, N> transformation);

        <N> OptionalPipelineBuilder<I, N> flatTransform(PipelineTransformation<C, Optional<N>> transformation);

        <O> LookupPipelineBuilder<I, C, O> lookup(Function<InitialPipelineBuilder<C>, Pipeline<C, Optional<O>>> lookup);
    }

    sealed interface OptionalPipelineBuilder<I, C> extends BuildablePipelineBuilder<I, Optional<C>> permits OptionalPipelineBuilderImpl {
        OptionalPipelineBuilder<I, C> filter(PipelineFilter<C> filter);

        <N> OptionalPipelineBuilder<I, N> transform(PipelineTransformation<C, N> transformation);

        <N> OptionalPipelineBuilder<I, N> flatTransform(PipelineTransformation<C, Optional<N>> transformation);

        <O> LookupPipelineBuilder<I, C, O> lookup(Function<InitialPipelineBuilder<C>, Pipeline<C, Optional<O>>> lookup);
    }

    sealed interface LookupPipelineBuilder<I, C, O> extends BuildablePipelineBuilder<I, Optional<O>> permits LookupPipelineBuilderImpl {

        LookupPipelineBuilder<I, C, O> filter(PipelineFilter<C> filter);

        <N> LookupPipelineBuilder<I, N, O> transform(PipelineTransformation<C, N> transformation);

        <N> LookupPipelineBuilder<I, N, O> flatTransform(PipelineTransformation<C, Optional<N>> transformation);

        LookupPipelineBuilder<I, C, O> lookup(Function<InitialPipelineBuilder<C>, Pipeline<C, Optional<O>>> lookup);
    }
}
