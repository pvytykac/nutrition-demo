package net.pvytykac.nutrition.pipeline.stage.transform;

import java.util.Optional;
import java.util.stream.Stream;

public interface PipelineTransformation<I, O> {

    O apply(I input);

    static <I, O> PipelineTransformation<I, Optional<O>> any(PipelineTransformation<I, Optional<O>>... transformations) {
        return input -> Stream.of(transformations)
                .map(t -> t.apply(input))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .findFirst();
    }
}
