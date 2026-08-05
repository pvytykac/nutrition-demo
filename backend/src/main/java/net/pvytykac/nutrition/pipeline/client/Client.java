package net.pvytykac.nutrition.pipeline.client;

import io.micrometer.observation.Observations;
import net.pvytykac.nutrition.pipeline.builder.PipelineBuilder;
import net.pvytykac.nutrition.pipeline.stage.StageTracing;
import net.pvytykac.nutrition.pipeline.stage.filter.ModelNameMaxLengthFilterStage;
import net.pvytykac.nutrition.pipeline.stage.filter.ModelNameMinLengthFilterStage;
import net.pvytykac.nutrition.pipeline.stage.filter.ModelNameStartsWithFilterStage;
import net.pvytykac.nutrition.pipeline.stage.filter.ThrowingFilterStage;
import net.pvytykac.nutrition.pipeline.stage.transform.PipelineTransformation;
import net.pvytykac.nutrition.pipeline.stage.transform.ReverseStringsStage;
import net.pvytykac.nutrition.pipeline.stage.transform.RollAndReturnOnMatchTransformStage;

/**
 * @author Paly
 * @since 2026-08-04
 */
public class Client {

    static void main() {
        var registry = Observations.getGlobalRegistry();
        var tracing = new StageTracing(registry);

        var pipeline = PipelineBuilder.<HardwareModel>builder()
//                .filter(tracing.traced(new ThrowingFilterStage<>()))
                .filter(tracing.traced(new ModelNameMaxLengthFilterStage(6))
                        .and(tracing.traced(new ModelNameMinLengthFilterStage(6))))
                .transform(tracing.traced(new ReverseStringsStage()))
                .filter(tracing.traced(new ModelNameStartsWithFilterStage("y"))
                        .or(tracing.traced(new ModelNameStartsWithFilterStage("e"))))
                .lookup(builder -> builder.transform(PipelineTransformation.any(
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(5)),
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(10)),
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(15)),
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(20)),
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(25))))
                        .build())
                .transform(tracing.traced(new ReverseStringsStage()))
                .filter(tracing.traced(new ModelNameStartsWithFilterStage("G"))
                        .or(tracing.traced(new ModelNameStartsWithFilterStage("e"))))
                .lookup(builder -> builder.transform(PipelineTransformation.any(
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(22)),
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(17)),
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(12)),
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(7)),
                                tracing.traced(new RollAndReturnOnMatchTransformStage<>(2))))
                        .build())
                .build();

        System.out.println(pipeline.execute(new HardwareModel("Apple", "iPhone 12")));
        System.out.println(pipeline.execute(new HardwareModel("Samsung", "Galaxy S24")));
        System.out.println(pipeline.execute(new HardwareModel("Apple", "iPhone")));
        System.out.println(pipeline.execute(new HardwareModel("Samsung", "Galaxy")));
    }

}
