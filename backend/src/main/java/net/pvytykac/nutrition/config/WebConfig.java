package net.pvytykac.nutrition.config;

import net.pvytykac.nutrition.ingredient.Unit;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Web configuration for custom converters and formatters.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // Register converter for Unit enum
        registry.addConverter(String.class, Unit.class, source -> {
            if (source == null || source.isBlank()) {
                return null;
            }
            return Unit.valueOf(source.toUpperCase());
        });

        // Register converter for List<Unit> - handles collection binding
        registry.addConverter(new Converter<String, List<Unit>>() {
            @Override
            public List<Unit> convert(String source) {
                if (source == null || source.isBlank()) {
                    return null;
                }
                return Arrays.asList(Unit.valueOf(source.toUpperCase()));
            }
        });
    }
}
