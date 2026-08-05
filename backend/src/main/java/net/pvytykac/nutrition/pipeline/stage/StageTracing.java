package net.pvytykac.nutrition.pipeline.stage;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.pipeline.stage.filter.PipelineFilter;
import net.pvytykac.nutrition.pipeline.stage.filter.TracedPipelineFilter;
import net.pvytykac.nutrition.pipeline.stage.transform.PipelineTransformation;
import net.pvytykac.nutrition.pipeline.stage.transform.TracedPipelineTransformation;

import java.util.function.Supplier;

/**
 * @author Paly
 * @since 2026-08-05
 */
@RequiredArgsConstructor
public class StageTracing {

    private final ObservationRegistry registry;

    public <I> PipelineFilter<I> traced(Stage.FilterStage<I> stage) {
        return new TracedPipelineFilter<>(this, stage);
    }

    public <I, O> PipelineTransformation<I, O> traced(Stage.TransformStage<I, O> stage) {
        return new TracedPipelineTransformation<>(this, stage);
    }

    public <I, O> O observe(Stage stage, I input, Supplier<O> stageFunction) {
        var observation = Observation.createNotStarted("pipelineStage", registry)
                .lowCardinalityKeyValue("type", stage.getType().toString())
                .lowCardinalityKeyValue("stage", stage.getId())
                .highCardinalityKeyValue("input", input.toString());

        return observation.observe(() -> {
            var result = stageFunction.get();
            observation.lowCardinalityKeyValue("output", result.toString());
            return result;
        });
    }

}
