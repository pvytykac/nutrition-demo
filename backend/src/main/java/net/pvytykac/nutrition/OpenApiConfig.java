package net.pvytykac.nutrition;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiInfo() {
        final String securitySchemeName = "bearerAuth";

        OAuthFlows oauthFlows = new OAuthFlows();
        OAuthFlow authorizationCodeFlow = new OAuthFlow()
                .authorizationUrl("http://localhost:8000/realms/nutrition/protocol/openid-connect/auth")
                .tokenUrl("http://localhost:8000/realms/nutrition/protocol/openid-connect/token")
                .refreshUrl("http://localhost:8000/realms/nutrition/protocol/openid-connect/token")
                .scopes(new Scopes());
        
        oauthFlows.authorizationCode(authorizationCodeFlow);

        return new OpenAPI()
                .info(new Info()
                        .title("Nutrition Demo API")
                        .version("1.0.0")
                        .description("REST APIs allowing users to manage lists of ingredients and recipes with the goal of keeping track of their daily nutrition details. Supports tracking of fats, carbs, protein, and amino acids (especially phenylalanine for PKU)."))
                .addSecurityItem(new SecurityRequirement()
                        .addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.OAUTH2)
                                        .flows(oauthFlows)));
    }
}
