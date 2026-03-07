package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.shared.exceptions.ResourceNotFoundException;
import net.pvytykac.nutrition.util.filtering.NumberOperator;
import net.pvytykac.nutrition.util.filtering.StringOperator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(IngredientController.class)
@AutoConfigureWebTestClient
@DisplayName("IngredientController")
class IngredientControllerTest {

    private static final UUID TEST_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IngredientService ingredientService;

    private IngredientResponseDTO createResponseDTO(UUID id, String name) {
        return IngredientResponseDTO.builder()
                .id(id)
                .name(name)
                .nutritionDetails(NutritionDetailsResponseDTO.builder()
                        .fat(new BigDecimal("10.5"))
                        .carbs(new BigDecimal("20.0"))
                        .protein(new BigDecimal("15.0"))
                        .phenylalanine(new BigDecimal("5.0"))
                        .unit("100g")
                        .build())
                .build();
    }

    private IngredientRequestDTO createRequestDTO(String name) {
        return IngredientRequestDTO.builder()
                .name(name)
                .nutritionDetails(NutritionDetailsRequestDTO.builder()
                        .fat(new BigDecimal("10.5"))
                        .carbs(new BigDecimal("20.0"))
                        .protein(new BigDecimal("15.0"))
                        .phenylalanine(new BigDecimal("5.0"))
                        .unit("100g")
                        .build())
                .build();
    }

    @Nested
    @DisplayName("POST /v1/ingredients")
    class CreateIngredient {

        @Test
        @DisplayName("should return 201 Created with ingredient on success")
        void shouldReturn201CreatedWithIngredient() throws Exception {
            // given
            IngredientRequestDTO request = createRequestDTO("Chicken Breast");
            IngredientResponseDTO response = createResponseDTO(TEST_ID, "Chicken Breast");
            when(ingredientService.createIngredient(any())).thenReturn(response);

            // when/then
            webTestClient.post()
                    .uri("/v1/ingredients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(request))
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(TEST_ID.toString())
                    .jsonPath("$.name").isEqualTo("Chicken Breast");
        }

        @Test
        @DisplayName("should return 400 Bad Request when name is missing")
        void shouldReturn400WhenNameMissing() throws Exception {
            // given
            Map<String, Object> request = Map.of(
                    "nutritionDetails", Map.of(
                            "fat", 10.5,
                            "carbs", 20.0,
                            "protein", 15.0,
                            "phenylalanine", 5.0,
                            "unit", "100g"
                    )
            );

            // when/then
            webTestClient.post()
                    .uri("/v1/ingredients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(request))
                    .exchange()
                    .expectStatus().isBadRequest();
        }
    }

    @Nested
    @DisplayName("GET /v1/ingredients/{id}")
    class GetIngredientById {

        @Test
        @DisplayName("should return 200 OK with ingredient when found")
        void shouldReturn200WhenFound() {
            // given
            IngredientResponseDTO response = createResponseDTO(TEST_ID, "Chicken Breast");
            when(ingredientService.getIngredientById(TEST_ID)).thenReturn(response);

            // when/then
            webTestClient.get()
                    .uri("/v1/ingredients/{id}", TEST_ID)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(TEST_ID.toString())
                    .jsonPath("$.name").isEqualTo("Chicken Breast");
        }

        @Test
        @DisplayName("should return 404 Not Found when not found")
        void shouldReturn404WhenNotFound() {
            // given
            when(ingredientService.getIngredientById(NON_EXISTENT_ID))
                    .thenThrow(new ResourceNotFoundException("Ingredient", NON_EXISTENT_ID));

            // when/then
            webTestClient.get()
                    .uri("/v1/ingredients/{id}", NON_EXISTENT_ID)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("GET /v1/ingredients")
    class GetAllIngredients {

        @Test
        @DisplayName("should return 200 OK with paginated content")
        void shouldReturn200WithPagedContent() {
            // given
            IngredientResponseDTO response = createResponseDTO(TEST_ID, "Chicken Breast");
            PageImpl<IngredientResponseDTO> page = new PageImpl<>(List.of(response));
            when(ingredientService.searchIngredients(
                    eq(null), eq(null), eq(null), eq(null), eq(null), any(Pageable.class)))
                    .thenReturn(page);

            // when/then
            webTestClient.get()
                    .uri("/v1/ingredients")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content").isArray()
                    .jsonPath("$.content[0].name").isEqualTo("Chicken Breast")
                    .jsonPath("$.totalElements").isEqualTo(1)
                    .jsonPath("$.totalPages").isEqualTo(1);
        }

        @Test
        @DisplayName("should return 200 OK with filtered results")
        void shouldReturn200WithFilteredResults() {
            // given
            PageImpl<IngredientResponseDTO> page = new PageImpl<>(List.of());
            when(ingredientService.searchIngredients(
                    eq("chicken"), eq(StringOperator.CONTAINS), 
                    eq(null), eq(null), eq(null), any(Pageable.class)))
                    .thenReturn(page);

            // when/then
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/v1/ingredients")
                            .queryParam("nameValue", "chicken")
                            .queryParam("nameOperator", "CONTAINS")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content").isArray();
        }

        @Test
        @DisplayName("should return 200 OK with paging parameters")
        void shouldReturn200WithPagingParameters() {
            // given
            PageImpl<IngredientResponseDTO> page = new PageImpl<>(List.of(), 
                    PageRequest.of(0, 10), 0);
            when(ingredientService.searchIngredients(
                    eq(null), eq(null), 
                    eq(new BigDecimal("5")), eq(null), eq(NumberOperator.GREATER_THAN), 
                    any(Pageable.class)))
                    .thenReturn(page);

            // when/then
            webTestClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/v1/ingredients")
                            .queryParam("phenylalanineValue", "5")
                            .queryParam("phenylalanineOperator", "GREATER_THAN")
                            .queryParam("page", "0")
                            .queryParam("size", "10")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.pageNumber").isEqualTo(0)
                    .jsonPath("$.pageSize").isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("PUT /v1/ingredients/{id}")
    class UpdateIngredient {

        @Test
        @DisplayName("should return 200 OK with updated ingredient")
        void shouldReturn200OnSuccess() throws Exception {
            // given
            IngredientRequestDTO request = createRequestDTO("Chicken Breast Updated");
            IngredientResponseDTO response = createResponseDTO(TEST_ID, "Chicken Breast Updated");
            when(ingredientService.updateIngredient(eq(TEST_ID), any())).thenReturn(response);

            // when/then
            webTestClient.put()
                    .uri("/v1/ingredients/{id}", TEST_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(request))
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(TEST_ID.toString())
                    .jsonPath("$.name").isEqualTo("Chicken Breast Updated");
        }

        @Test
        @DisplayName("should return 404 Not Found when ingredient doesn't exist")
        void shouldReturn404WhenNotFound() throws Exception {
            // given
            IngredientRequestDTO request = createRequestDTO("NonExistent");
            when(ingredientService.updateIngredient(eq(NON_EXISTENT_ID), any()))
                    .thenThrow(new ResourceNotFoundException("Ingredient", NON_EXISTENT_ID));

            // when/then
            webTestClient.put()
                    .uri("/v1/ingredients/{id}", NON_EXISTENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(objectMapper.writeValueAsString(request))
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }

    @Nested
    @DisplayName("DELETE /v1/ingredients/{id}")
    class DeleteIngredient {

        @Test
        @DisplayName("should return 204 No Content on success")
        void shouldReturn204OnSuccess() {
            // given
            doNothing().when(ingredientService).deleteIngredient(TEST_ID);

            // when/then
            webTestClient.delete()
                    .uri("/v1/ingredients/{id}", TEST_ID)
                    .exchange()
                    .expectStatus().isNoContent();
            
            verify(ingredientService).deleteIngredient(TEST_ID);
        }

        @Test
        @DisplayName("should return 404 Not Found when ingredient doesn't exist")
        void shouldReturn404WhenNotFound() {
            // given
            doThrow(new ResourceNotFoundException("Ingredient", NON_EXISTENT_ID))
                    .when(ingredientService).deleteIngredient(NON_EXISTENT_ID);

            // when/then
            webTestClient.delete()
                    .uri("/v1/ingredients/{id}", NON_EXISTENT_ID)
                    .exchange()
                    .expectStatus().isNotFound();
        }
    }
}
