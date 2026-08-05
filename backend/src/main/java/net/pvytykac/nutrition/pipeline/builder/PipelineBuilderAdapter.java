package net.pvytykac.nutrition.pipeline.builder;

import net.pvytykac.nutrition.pipeline.stage.filter.PipelineFilter;
import net.pvytykac.nutrition.pipeline.stage.transform.PipelineTransformation;

import java.util.Optional;
import java.util.function.Function;

/**
 * @author Paly
 * @since 2026-08-05
 */
final class PipelineBuilderAdapter {

    private PipelineBuilderAdapter() {
    }

    static <C> Function<C, Optional<C>> filterFunction(PipelineFilter<C> filter) {
        return c -> filter.matches(c) ? Optional.of(c) : Optional.empty();
    }

    static <C> Function<Optional<C>, Optional<C>> optionalFilterFunction(PipelineFilter<C> filter) {
        var inner = filterFunction(filter);
        return c -> c.flatMap(inner);
    }

    static <C, N> Function<C, N> transformFunction(PipelineTransformation<C, N> transform) {
        return transform::apply;
    }

    static <C, N> Function<Optional<C>, Optional<N>> optionalTransformFunction(PipelineTransformation<C, N> transform) {
        var inner = transformFunction(transform);
        return c -> c.map(inner);
    }

    static <C, N> Function<Optional<C>, Optional<N>> flatTransformFunction(PipelineTransformation<C, Optional<N>> transform) {
        var inner = transformFunction(transform);
        return c -> c.flatMap(inner);
    }
}
