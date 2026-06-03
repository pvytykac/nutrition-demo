package net.pvytykac.nutrition.common;

import lombok.AccessLevel;
import lombok.Getter;
import net.pvytykac.nutrition.common.WebTestClientConfiguration.RestHelper;
import net.pvytykac.nutrition.common.security.TestJwtDecoderConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Import({TestJwtDecoderConfiguration.class, WebTestClientConfiguration.class})
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:tc:postgresql:18:///integration-test",
        "spring.datasource.driver-class-name=org.testcontainers.jdbc.ContainerDatabaseDriver"
})
public abstract class IntegrationTestBase {

    @Getter(AccessLevel.PROTECTED)
    @Autowired
    private RestHelper restHelper;

}
