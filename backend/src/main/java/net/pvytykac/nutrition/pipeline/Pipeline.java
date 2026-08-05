package net.pvytykac.nutrition.pipeline;

import net.pvytykac.nutrition.pipeline.stage.Stage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static net.pvytykac.nutrition.pipeline.TracedPipelineImpl.CONTEXT_BUILDER;

/**
 * @author Paly
 * @since 2026-08-04
 */
public interface Pipeline<I, O> {

    static PipelineImpl.ContextBuilder getContextBuilder() {
        return CONTEXT_BUILDER.get();
    }

    static <I, O> Pipeline<I, O> untraced(Function<I, O> function) {
        return new PipelineImpl<>(function);
    }

    static <I, O> Pipeline<I, O> traced(Function<I, O> function) {
        return new TracedPipelineImpl<>(new PipelineImpl<>(function));
    }

    Output<O> execute(I input);

    Function<I, O> toFunction();

    record Output<T>(T result, Context context) {
    }

    record Context(Map<String, Object> debugInfo) {
    }

    class ContextBuilder {

        private final Map<String, Object> debugInfo = new HashMap<>();

        public ContextBuilder debugInfo(Stage stage, Object data) {
            this.debugInfo.put(stage.getId(), data);
            return this;
        }

        public Context build() {
            return new Context(Map.copyOf(debugInfo));
        }
    }
}
