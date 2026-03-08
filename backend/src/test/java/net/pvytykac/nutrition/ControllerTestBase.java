package net.pvytykac.nutrition;

import net.pvytykac.nutrition.config.SecurityConfig;
import net.pvytykac.nutrition.config.TestJwtDecoderConfig;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
@Import({SecurityConfig.class, TestJwtDecoderConfig.class})
public abstract class ControllerTestBase {

    @Autowired
    protected WebTestClient webTestClient;

    protected WebTestClient withAdminAuth() {
        return webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer mock-token-admin")
                .build();
    }

    protected WebTestClient withUserAuth() {
        return webTestClient.mutate()
                .defaultHeader("Authorization", "Bearer mock-token-user")
                .build();
    }
}
