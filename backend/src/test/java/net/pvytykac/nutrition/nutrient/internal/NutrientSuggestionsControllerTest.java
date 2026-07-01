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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = NutrientSuggestionsController.class)
@Import(NutrientSuggestionsControllerTest.MockConfig.class)
public class NutrientSuggestionsControllerTest extends ControllerTestBase {

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

    private SuggestionResponseDTO createSuggestionResponse(UUID id) {
        return SuggestionResponseDTO.builder()
                .id(id)
                .name("Vitamin C")
                .kcalPerGram(new BigDecimal("0.5000"))
                .defaultUnit(NutrientUnit.MILLIGRAM)
                .status(NutrientStatus.SUGGESTED)
                .source(NutrientSource.SUGGESTION)
                .authorId("user1")
                .voteCount(3)
                .createdAt(Instant.now())
                .build();
    }

    @Test
    void suggestShouldReturn201WhenUser() {
        var id = UUID.randomUUID();
        var response = createSuggestionResponse(id);
        when(MockConfig.nutrientService.suggestNutrient(any(), any())).thenReturn(response);
        when(MockConfig.nutrientLinkBuilder.buildSuggestionResourceLinks(any(), any(), eq(false))).thenReturn(List.of());

        getRestHelper()
                .withUserAuth()
                .post()
                .uri("/nutrient-suggestions")
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "Vitamin C",
                            "kcalPerGram": 0.5,
                            "defaultUnit": "MILLIGRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Vitamin C")
                .jsonPath("$.status").isEqualTo("SUGGESTED")
                .jsonPath("$.source").isEqualTo("SUGGESTION");
    }

    @Test
    void suggestShouldReturn403WhenAdmin() {
        getRestHelper()
                .withAdminAuth()
                .post()
                .uri("/nutrient-suggestions")
                .apiVersion("v1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {
                            "name": "Vitamin C",
                            "kcalPerGram": 0.5,
                            "defaultUnit": "MILLIGRAM"
                        }
                        """)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void getShouldReturn200WhenUser() {
        var response = createSuggestionResponse(UUID.randomUUID());
        var page = new PageImpl<>(List.of(response));
        when(MockConfig.nutrientService.findAllSuggestions(any())).thenReturn(page);
        when(MockConfig.nutrientLinkBuilder.buildSuggestionCollectionLinks(any())).thenReturn(List.of());

        getRestHelper()
                .withUserAuth()
                .get()
                .uri("/nutrient-suggestions")
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getByIdShouldReturn200WhenExists() {
        var id = UUID.randomUUID();
        var response = createSuggestionResponse(id);
        when(MockConfig.nutrientService.findSuggestionById(id)).thenReturn(response);
        when(MockConfig.nutrientService.hasVoted(id, "testuser")).thenReturn(false);
        when(MockConfig.nutrientLinkBuilder.buildSuggestionResourceLinks(any(), any(), eq(false))).thenReturn(List.of());

        getRestHelper()
                .withUserAuth()
                .get()
                .uri("/nutrient-suggestions/" + id)
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Vitamin C");
    }

    @Test
    void getByIdShouldReturn404WhenNotFound() {
        var id = UUID.randomUUID();
        when(MockConfig.nutrientService.findSuggestionById(id)).thenThrow(new ResourceNotFoundException("Suggestion", id));

        getRestHelper()
                .withUserAuth()
                .get()
                .uri("/nutrient-suggestions/" + id)
                .apiVersion("v1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void voteShouldReturn200WhenUser() {
        var id = UUID.randomUUID();
        var response = createSuggestionResponse(id);
        response.setVoteCount(4);
        when(MockConfig.nutrientService.voteOnSuggestion(eq(id), any())).thenReturn(response);
        when(MockConfig.nutrientLinkBuilder.buildSuggestionResourceLinks(any(), any(), eq(true))).thenReturn(List.of());

        getRestHelper()
                .withUserAuth()
                .post()
                .uri("/nutrient-suggestions/" + id + "/votes")
                .apiVersion("v1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.voteCount").isEqualTo(4);
    }

    @Test
    void approveShouldReturn200WhenAdmin() {
        var id = UUID.randomUUID();
        var response = createSuggestionResponse(id);
        response.setStatus(NutrientStatus.ACTIVE);
        when(MockConfig.nutrientService.approveSuggestion(id)).thenReturn(response);
        when(MockConfig.nutrientLinkBuilder.buildSuggestionResourceLinks(any(), any(), eq(false))).thenReturn(List.of());

        getRestHelper()
                .withAdminAuth()
                .post()
                .uri("/nutrient-suggestions/" + id + "/approve")
                .apiVersion("v1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo("ACTIVE");
    }

    @Test
    void approveShouldReturn403WhenUser() {
        var id = UUID.randomUUID();
        getRestHelper()
                .withUserAuth()
                .post()
                .uri("/nutrient-suggestions/" + id + "/approve")
                .apiVersion("v1")
                .exchange()
                .expectStatus().isForbidden();
    }
}
