package net.pvytykac.nutrition.config;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class IntegrationTestBase {

    protected static PostgreSQLContainer<?> postgresContainer;

    @BeforeAll
    static void startContainer() {
        if (postgresContainer == null) {
            postgresContainer = new PostgreSQLContainer<>("postgres:18")
                    .withDatabaseName("nutrition_test")
                    .withUsername("test")
                    .withPassword("test");
            postgresContainer.start();
            
            log.info("PostgreSQL container started on port: {}", postgresContainer.getMappedPort(5432));
            
            // Set system properties for the application to use
            System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
            System.setProperty("spring.datasource.username", postgresContainer.getUsername());
            System.setProperty("spring.datasource.password", postgresContainer.getPassword());
        }
    }
}
