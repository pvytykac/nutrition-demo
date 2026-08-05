package net.pvytykac.nutrition.pipeline.stage;

import net.pvytykac.nutrition.pipeline.stage.filter.PipelineFilter;
import net.pvytykac.nutrition.pipeline.stage.transform.PipelineTransformation;

/**
 * @author Paly
 * @since 2026-08-04
 */
public interface Stage {

    String getId();

    Type getType();

    enum Type {
        FILTER, TRANSFORM
    }

    interface FilterStage<I> extends Stage, PipelineFilter<I> {
        @Override
        default Type getType() {
            return Type.FILTER;
        }
    }

    interface TransformStage<I, O> extends Stage, PipelineTransformation<I, O> {
        @Override
        default Type getType() {
            return Type.TRANSFORM;
        }
    }
}
