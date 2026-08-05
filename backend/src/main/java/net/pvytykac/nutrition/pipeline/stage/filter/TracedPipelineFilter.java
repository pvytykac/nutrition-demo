package net.pvytykac.nutrition.pipeline.stage.filter;

import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.pipeline.stage.Stage;
import net.pvytykac.nutrition.pipeline.stage.StageTracing;

@RequiredArgsConstructor
public class TracedPipelineFilter<I> implements PipelineFilter<I> {
    private final StageTracing tracing;
    private final Stage.FilterStage<I> delegate;

    @Override
    public boolean matches(I input) {
        return tracing.observe(delegate, input, () -> delegate.matches(input));
    }
}