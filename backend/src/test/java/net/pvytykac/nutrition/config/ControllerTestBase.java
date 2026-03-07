package net.pvytykac.nutrition.config;

import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.TestPropertySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient
public abstract class ControllerTestBase {

    @Autowired
    protected WebTestClient webTestClient;
}
