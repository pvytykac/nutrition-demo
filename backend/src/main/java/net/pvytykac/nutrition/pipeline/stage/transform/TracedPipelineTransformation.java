package net.pvytykac.nutrition.pipeline.stage.transform;

import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.pipeline.stage.Stage;
import net.pvytykac.nutrition.pipeline.stage.StageTracing;

@RequiredArgsConstructor
public class TracedPipelineTransformation<I, O> implements PipelineTransformation<I, O> {

    private final StageTracing tracing;
    private final Stage.TransformStage<I, O> delegate;

    @Override
    public O apply(I input) {
        return tracing.observe(delegate, input, () -> delegate.apply(input));
    }
}