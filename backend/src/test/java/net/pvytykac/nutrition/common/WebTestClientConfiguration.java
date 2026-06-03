package net.pvytykac.nutrition.common;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.ApiVersionInserter;

@TestConfiguration
public class WebTestClientConfiguration {

    @Bean
    public ApiVersionInserter apiVersionInserter() {
        return ApiVersionInserter.builder()
                .usePathSegment(0)
                .build();
    }

    @Bean
    public RestHelper webTestClientHelper(WebTestClient webTestClient, ApiVersionInserter apiVersionInserter) {
        return new RestHelper(webTestClient, apiVersionInserter);
    }

    @RequiredArgsConstructor
    public static class RestHelper {

        private final WebTestClient webTestClient;
        private final ApiVersionInserter apiVersionInserter;

        public WebTestClient unauthenticated() {
            return defaultBuilder().build();
        }

        public WebTestClient withAdminAuth() {
            return defaultBuilder()
                    .defaultHeader("Authorization", "Bearer mock-token-admin")
                    .build();
        }

        public WebTestClient withUserAuth() {
            return defaultBuilder()
                    .defaultHeader("Authorization", "Bearer mock-token-user")
                    .build();
        }

        private WebTestClient.Builder defaultBuilder() {
            return webTestClient.mutate()
                    .apiVersionInserter(apiVersionInserter);
        }
    }
}
