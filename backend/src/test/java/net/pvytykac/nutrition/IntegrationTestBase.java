package net.pvytykac.nutrition;

import net.pvytykac.nutrition.util.security.TestJwtDecoderConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import(TestJwtDecoderConfig.class)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:tc:postgresql:18:///integration-test",
    "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
public abstract class IntegrationTestBase {

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
