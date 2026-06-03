package net.pvytykac.nutrition;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuer;

    @Bean
    public OpenAPI apiInfo() {
        final String securitySchemeName = "bearerAuth";

        OAuthFlows oauthFlows = new OAuthFlows();
        OAuthFlow authorizationCodeFlow = new OAuthFlow()
                .authorizationUrl(issuer + "/protocol/openid-connect/auth")
                .tokenUrl(issuer + "/protocol/openid-connect/token")
                .refreshUrl(issuer + "/protocol/openid-connect/token")
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
