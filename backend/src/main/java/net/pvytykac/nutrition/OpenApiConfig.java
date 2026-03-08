package net.pvytykac.nutrition;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Nutrition Demo API")
                        .version("1.0.0")
                        .description("REST APIs allowing users to manage lists of ingredients and recipes with the goal of keeping track of their daily nutrition details. Supports tracking of fats, carbs, protein, and amino acids (especially phenylalanine for PKU)."));
    }
}
