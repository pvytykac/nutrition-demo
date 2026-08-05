package net.pvytykac.nutrition.pipeline.builder;

import net.pvytykac.nutrition.pipeline.Pipeline;
import net.pvytykac.nutrition.pipeline.stage.filter.PipelineFilter;
import net.pvytykac.nutrition.pipeline.stage.transform.PipelineTransformation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * @author Paly
 * @since 2026-08-05
 */
public final class LookupPipelineBuilderImpl<I, C, O> implements PipelineBuilder.LookupPipelineBuilder<I, C, O> {

    private final Function<I, Optional<Either<C, O>>> root;
    private final List<Function<C, Optional<O>>> lookups;

    static <I, C, O> LookupPipelineBuilder<I, C, O> of(Function<I, Optional<C>> root, Function<C, Optional<O>> lookup) {
        return new LookupPipelineBuilderImpl<>(root.andThen(c -> c.map(Either::left)), lookup);
    }

    private LookupPipelineBuilderImpl(Function<I, Optional<Either<C, O>>> root) {
        this.root = root;
        this.lookups = new ArrayList<>();
    }

    private LookupPipelineBuilderImpl(Function<I, Optional<Either<C, O>>> root, Function<C, Optional<O>> lookup) {
        this(root);
        lookups.add(lookup);
    }

    @Override
    public LookupPipelineBuilder<I, C, O> filter(PipelineFilter<C> filter) {
        var filterFunction = PipelineBuilderAdapter.filterFunction(filter);

        Function<Optional<Either<C, O>>, Optional<Either<C, O>>> mergedRootFunction = current -> current.flatMap(either -> switch (either) {
            case Either.Left<C, O> left -> filterFunction.apply(left.value()).map(Either::<C, O>left);
            case Either.Right<C, O> right -> Optional.of(right);
        });

        return new LookupPipelineBuilderImpl<>(mergeFunctions().andThen(mergedRootFunction));
    }

    @Override
    public <N> LookupPipelineBuilder<I, N, O> transform(PipelineTransformation<C, N> transformation) {
        var transformFunction = PipelineBuilderAdapter.transformFunction(transformation);

        Function<Optional<Either<C, O>>, Optional<Either<N, O>>> mergedRootFunction = current -> current.map(either -> switch (either) {
            case Either.Left<C, O> left -> Either.<N, O>left(transformFunction.apply(left.value()));
            case Either.Right<C, O> right -> Either.<N, O>right(right.value());
        });

        return new LookupPipelineBuilderImpl<>(mergeFunctions().andThen(mergedRootFunction));
    }

    @Override
    public <N> LookupPipelineBuilder<I, N, O> flatTransform(PipelineTransformation<C, Optional<N>> transformation) {
        var transformFunction = PipelineBuilderAdapter.transformFunction(transformation);

        Function<Optional<Either<C, O>>, Optional<Either<N, O>>> mergedRootFunction = current -> current.flatMap(either ->
                switch (either) {
                    case Either.Left<C, O> left -> transformFunction.apply(left.value()).map(Either::<N, O>left);
                    case Either.Right<C, O> right -> Optional.of(Either.<N, O>right(right.value()));
                });

        return new LookupPipelineBuilderImpl<>(mergeFunctions().andThen(mergedRootFunction));
    }

    @Override
    public LookupPipelineBuilder<I, C, O> lookup(Function<InitialPipelineBuilder<C>, Pipeline<C, Optional<O>>> lookup) {
        lookups.add(lookup.apply(new InitialPipelineBuilderImpl<>()).toFunction());
        return this;
    }

    @Override
    public Pipeline<I, Optional<O>> build() {
        return Pipeline.traced(mergeFunctions().andThen(either -> either.flatMap(someEither -> switch (someEither) {
            case Either.Right<C, O> right -> Optional.of(right.value());
            case Either.Left<C, O> left -> Optional.empty();
        })));
    }

    private Function<I, Optional<Either<C, O>>> mergeFunctions() {
        return root.andThen(c -> {
            if (c.isEmpty()) {
                return c;
            }

            Either<C, O> either = switch (c.get()) {
                case Either.Left<C, O> current -> lookups.stream().map(lookup -> lookup.apply(current.value()))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst()
                        .<Either<C, O>>map(Either::right)
                        .orElse(current);
                case Either.Right<C, O> output -> output;
            };

            return Optional.of(either);
        });
    }
}
