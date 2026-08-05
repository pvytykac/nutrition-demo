package net.pvytykac.nutrition.pipeline;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;

/**
 * @author Paly
 * @since 2026-08-04
 */
@RequiredArgsConstructor
class TracedPipelineImpl<I, O> implements Pipeline<I, O> {

    static ScopedValue<ContextBuilder> CONTEXT_BUILDER = ScopedValue.newInstance();

    private final Pipeline<I, O> delegate;

    @Override
    public Output<O> execute(I input) {
        return ScopedValue.where(CONTEXT_BUILDER, new ContextBuilder())
                .call(() -> new Output<>(delegate.execute(input).result(), CONTEXT_BUILDER.get().build()));
    }

    @Override
    public Function<I, O> toFunction() {
        return delegate.toFunction();
    }
}
