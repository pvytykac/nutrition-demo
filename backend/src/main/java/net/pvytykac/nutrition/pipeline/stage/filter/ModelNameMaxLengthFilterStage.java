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
public class ModelNameMaxLengthFilterStage implements Stage.FilterStage<HardwareModel> {

    private final int maxLength;

    @Override
    public String getId() {
        return this.getClass().getSimpleName() + "#" + maxLength;
    }

    @Override
    public boolean matches(HardwareModel input) {
        var result = input.model().length() <= this.maxLength;

        Pipeline.getContextBuilder()
                .debugInfo(this, Map.of("input", input.model(), "maxLength", maxLength, "output", result));

        return result;
    }

}
