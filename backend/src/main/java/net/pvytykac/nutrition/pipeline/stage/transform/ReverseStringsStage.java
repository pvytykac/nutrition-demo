package net.pvytykac.nutrition.pipeline.stage.transform;

import net.pvytykac.nutrition.pipeline.Pipeline;
import net.pvytykac.nutrition.pipeline.client.HardwareModel;
import net.pvytykac.nutrition.pipeline.stage.Stage;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @author Paly
 * @since 2026-08-04
 */
public class ReverseStringsStage implements Stage.TransformStage<HardwareModel, HardwareModel> {

    @Override
    public String getId() {
        return this.toString();
    }

    @Override
    public HardwareModel apply(HardwareModel input) {
        var output = new HardwareModel(StringUtils.reverse(input.vendor()), StringUtils.reverse(input.model()));

        Pipeline.getContextBuilder()
                .debugInfo(this, Map.of("input", input, "output", output));

        return output;
    }
}
