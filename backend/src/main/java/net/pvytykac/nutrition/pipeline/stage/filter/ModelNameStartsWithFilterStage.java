package net.pvytykac.nutrition.pipeline.stage.filter;

import lombok.RequiredArgsConstructor;
import net.pvytykac.nutrition.pipeline.Pipeline;
import net.pvytykac.nutrition.pipeline.client.HardwareModel;
import net.pvytykac.nutrition.pipeline.stage.Stage;

import java.util.Map;

/**
 * @author Paly
 * @since 2026-08-04
 */
@RequiredArgsConstructor
public class ModelNameStartsWithFilterStage implements Stage.FilterStage<HardwareModel> {

    private final String prefix;

    @Override
    public String getId() {
        return this.getClass().getSimpleName() + "#" + this.prefix;
    }

    @Override
    public boolean matches(HardwareModel input) {
        var result = input.model().startsWith(this.prefix);

        Pipeline.getContextBuilder()
                .debugInfo(this, Map.of("input", input.model(), "prefix", prefix, "output", result));

        return result;
    }

}
