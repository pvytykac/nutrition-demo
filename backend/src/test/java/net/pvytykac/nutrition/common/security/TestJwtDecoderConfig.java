package net.pvytykac.nutrition.common.security;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@TestConfiguration
public class TestJwtDecoderConfig {

    @Bean
    @Primary
    public JwtDecoder jwtDecoder() {
        return token -> {
            if (token == null || token.isEmpty()) {
                throw new JwtException("Invalid token");
            }

            // Parse the token to extract role information from custom claim
            // Format: "mock-token-{role}" or use default structure
            String role = "user";
            if (token.contains("admin")) {
                role = "admin";
            } else if (token.contains("user")) {
                role = "user";
            }

            Map<String, Object> headers = Map.of("alg", "none");
            Map<String, Object> claims = Map.of(
                "sub", "test-user",
                "preferred_username", "testuser",
                "realm_access", Map.of("roles", Collections.singletonList(role)),
                "iat", Instant.now(),
                "exp", Instant.now().plusSeconds(3600)
            );

            return Jwt.withTokenValue(token)
                .headers(h -> h.putAll(headers))
                .claims(c -> c.putAll(claims))
                .build();
        };
    }
}
