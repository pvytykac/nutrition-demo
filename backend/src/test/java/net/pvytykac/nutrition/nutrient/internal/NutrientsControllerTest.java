package net.pvytykac.nutrition.nutrient.internal;

import net.pvytykac.nutrition.common.ControllerTestBase;
import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = NutrientsController.class)
@Import(NutrientsControllerTest.MockConfig.class)
public class NutrientsControllerTest extends ControllerTestBase {

    @TestConfiguration
    static class MockConfig {
        static final NutrientService nutrientService = mock(NutrientService.class);
        static final NutrientLinkBuilder nutrientLinkBuilder = mock(NutrientLinkBuilder.class);

        @Bean
        NutrientService nutrientService() {
            return nutrientService;
        }

        @Bean
        NutrientLinkBuilder nutrientLinkBuilder() {
            return nutrientLinkBuilder;
        }
    }

    @BeforeEach
    void resetMocks() {
        reset(MockConfig.nutrientService, MockConfig.nutrientLinkBuilder);
    }

    private NutrientResponseDTO createResponse(UUID id) {
        return NutrientResponseDTO.builder()
                .id(id)
                .name("Protein")
                .kcalPerGram(new BigDecimal("4.0000"))
                .defaultUnit(NutrientUnit.GRAM)
                .status(NutrientStatus.ACTIVE)
                .source(NutrientSource.ADMIN)
                .authorId("admin")
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void createShouldReturn201WhenAdmin() {
        var id = UUID.randomUUID();
        var response = createResponse(id);
        when(MockConfig.nutrientService.createNutrient(any(), any())).thenReturn(response);
        when(MockConfig.nutrientLinkBuilder.buildNutrientResourceLinks(any(), any())).thenReturn(List.of());

        getRestHelper()
                .withAdminAuth()
                .post()
                .uri("/nutrients")
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "Protein",
                            "kcalPerGram": 4.0,
                            "defaultUnit": "GRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Protein")
                .jsonPath("$.status").isEqualTo("ACTIVE")
                .jsonPath("$.source").isEqualTo("ADMIN");
    }

    @Test
    void createShouldReturn403WhenUser() {
        getRestHelper()
                .withUserAuth()
                .post()
                .uri("/nutrients")
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "Protein",
                            "kcalPerGram": 4.0,
                            "defaultUnit": "GRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void createShouldReturn401WhenUnauthenticated() {
        getRestHelper()
                .unauthenticated()
                .post()
                .uri("/nutrients")
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "Protein",
                            "kcalPerGram": 4.0,
                            "defaultUnit": "GRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void createShouldReturn400WhenInvalid() {
        getRestHelper()
                .withAdminAuth()
                .post()
                .uri("/nutrients")
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "kcalPerGram": 4.0,
                            "defaultUnit": "GRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getShouldReturn200WhenUser() {
        var response = createResponse(UUID.randomUUID());
        var page = new PageImpl<>(List.of(response));
        when(MockConfig.nutrientService.findAllNutrients(any(), any())).thenReturn(page);
        when(MockConfig.nutrientLinkBuilder.buildNutrientCollectionLinks(any())).thenReturn(List.of());

        getRestHelper()
                .withUserAuth()
                .get()
                .uri("/nutrients")
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getShouldReturn200WhenAdmin() {
        var response = createResponse(UUID.randomUUID());
        var page = new PageImpl<>(List.of(response));
        when(MockConfig.nutrientService.findAllNutrients(any(), any())).thenReturn(page);
        when(MockConfig.nutrientLinkBuilder.buildNutrientCollectionLinks(any())).thenReturn(List.of());

        getRestHelper()
                .withAdminAuth()
                .get()
                .uri("/nutrients")
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getShouldReturn401WhenUnauthenticated() {
        getRestHelper()
                .unauthenticated()
                .get()
                .uri("/nutrients")
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void getByIdShouldReturn200WhenExists() {
        var id = UUID.randomUUID();
        var response = createResponse(id);
        when(MockConfig.nutrientService.findNutrientById(id)).thenReturn(response);
        when(MockConfig.nutrientLinkBuilder.buildNutrientResourceLinks(any(), any())).thenReturn(List.of());

        getRestHelper()
                .withUserAuth()
                .get()
                .uri("/nutrients/" + id)
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Protein");
    }

    @Test
    void getByIdShouldReturn404WhenNotFound() {
        var id = UUID.randomUUID();
        when(MockConfig.nutrientService.findNutrientById(id)).thenThrow(new ResourceNotFoundException("Nutrient", id));

        getRestHelper()
                .withUserAuth()
                .get()
                .uri("/nutrients/" + id)
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void updateShouldReturn200WhenAdmin() {
        var id = UUID.randomUUID();
        var response = createResponse(id);
        when(MockConfig.nutrientService.updateNutrient(any(), any())).thenReturn(response);
        when(MockConfig.nutrientLinkBuilder.buildNutrientResourceLinks(any(), any())).thenReturn(List.of());

        getRestHelper()
                .withAdminAuth()
                .put()
                .uri("/nutrients/" + id)
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "Updated Protein",
                            "kcalPerGram": 5.0,
                            "defaultUnit": "GRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void updateShouldReturn403WhenUser() {
        var id = UUID.randomUUID();
        getRestHelper()
                .withUserAuth()
                .put()
                .uri("/nutrients/" + id)
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "Updated Protein",
                            "kcalPerGram": 5.0,
                            "defaultUnit": "GRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void updateShouldReturn404WhenNotFound() {
        var id = UUID.randomUUID();
        when(MockConfig.nutrientService.updateNutrient(any(), any())).thenThrow(new ResourceNotFoundException("Nutrient", id));

        getRestHelper()
                .withAdminAuth()
                .put()
                .uri("/nutrients/" + id)
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "Updated Protein",
                            "kcalPerGram": 5.0,
                            "defaultUnit": "GRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void deleteShouldReturn204WhenAdmin() {
        var id = UUID.randomUUID();
        doNothing().when(MockConfig.nutrientService).deleteNutrient(id);

        getRestHelper()
                .withAdminAuth()
                .delete()
                .uri("/nutrients/" + id)
                .apiVersion("v1")
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void deleteShouldReturn403WhenUser() {
        var id = UUID.randomUUID();
        getRestHelper()
                .withUserAuth()
                .delete()
                .uri("/nutrients/" + id)
                .apiVersion("v1")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void deleteShouldReturn404WhenNotFound() {
        var id = UUID.randomUUID();
        doThrow(new ResourceNotFoundException("Nutrient", id)).when(MockConfig.nutrientService).deleteNutrient(id);

        getRestHelper()
                .withAdminAuth()
                .delete()
                .uri("/nutrients/" + id)
                .apiVersion("v1")
                .exchange()
                .expectStatus().isNotFound();
    }
}
