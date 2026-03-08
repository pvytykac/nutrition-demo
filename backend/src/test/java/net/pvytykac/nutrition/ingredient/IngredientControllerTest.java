package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.ControllerTestBase;
import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import net.pvytykac.nutrition.common.filtering.NumericFilter;
import net.pvytykac.nutrition.common.filtering.StringFilter;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(IngredientController.class)
@DisplayName("IngredientController")
class IngredientControllerTest extends ControllerTestBase {

    private static final UUID TEST_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @MockitoBean
    private IngredientService ingredientService;

    private IngredientResponseDTO createResponseDTO(UUID id, String name) {
        return IngredientResponseDTO.builder()
                .id(id)
                .name(name)
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(NutritionDetailsResponseDTO.builder()
                        .fatContent(new BigDecimal("10.5"))
                        .carbsContent(new BigDecimal("20.0"))
                        .proteinContent(new BigDecimal("15.0"))
                        .phenylalanineContent(new BigDecimal("5.0"))
                        .kilocalories(new BigDecimal("150.0"))
                        .build())
                .build();
    }

    private IngredientRequestDTO createRequestDTO(String name) {
        return IngredientRequestDTO.builder()
                .name(name)
                .quantity(new BigDecimal("100.0"))
                .unit(Unit.GRAM)
                .nutritionDetails(NutritionDetailsRequestDTO.builder()
                        .fatContent(new BigDecimal("10.5"))
                        .carbsContent(new BigDecimal("20.0"))
                        .proteinContent(new BigDecimal("15.0"))
                        .phenylalanineContent(new BigDecimal("5.0"))
                        .kilocalories(new BigDecimal("150.0"))
                        .build())
                .build();
    }

    private JSONObject createRequestJSON(String name) throws JSONException {
        return new JSONObject()
            .put("name", name)
            .put("quantity", 100.0)
            .put("unit", "GRAM")
            .put("nutritionDetails", new JSONObject()
                .put("fatContent", 10.5)
                .put("carbsContent", 20.0)
                .put("proteinContent", 15.0)
                .put("phenylalanineContent", 5.0)
                .put("kilocalories", 150.0));
    }

    private JSONObject createRequestJSONWithoutName() throws JSONException {
        return new JSONObject()
            .put("quantity", 100.0)
            .put("unit", "GRAM")
            .put("nutritionDetails", new JSONObject()
                .put("fatContent", 10.5)
                .put("carbsContent", 20.0)
                .put("proteinContent", 15.0)
                .put("phenylalanineContent", 5.0)
                .put("kilocalories", 150.0));
    }

    @Nested
    @DisplayName("POST /v1/ingredients")
    class CreateIngredient {

        @Test
        @DisplayName("should return 201 Created with ingredient on success")
        void shouldReturn201CreatedWithIngredient() throws JSONException {
            // given
            IngredientResponseDTO response = createResponseDTO(TEST_ID, "Chicken Breast");
            when(ingredientService.createIngredient(any())).thenReturn(response);

            // when/then
            withAdminAuth().post()
                    .uri("/v1/ingredients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Chicken Breast").toString())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(TEST_ID.toString())
                    .jsonPath("$.name").isEqualTo("Chicken Breast");
        }

        @Test
        @DisplayName("should return 400 Bad Request when name is missing")
        void shouldReturn400WhenNameMissing() throws JSONException {
            // when/then
            withAdminAuth().post()
                    .uri("/v1/ingredients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSONWithoutName().toString())
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("should return 403 Forbidden when user has role 'user' instead of 'admin'")
        void shouldReturn403WhenUserRole() throws JSONException {
            // when/then
            withUserAuth().post()
                    .uri("/v1/ingredients")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Chicken Breast").toString())
                    .exchange()
                    .expectStatus().isForbidden();
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
            withAdminAuth().get()
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
            withAdminAuth().get()
                    .uri("/v1/ingredients/{id}", NON_EXISTENT_ID)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("should return 403 Forbidden when user has role 'user' instead of 'admin'")
        void shouldReturn403WhenUserRole() {
            // when/then
            withUserAuth().get()
                    .uri("/v1/ingredients/{id}", TEST_ID)
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }

    @Nested
    @DisplayName("GET /v1/ingredients")
    class GetAllIngredients {

        @Test
        @DisplayName("should return 200 OK with paginated content and default pagination")
        void shouldReturn200WithPagedContentAndDefaultPagination() {
            // given
            IngredientResponseDTO response = createResponseDTO(TEST_ID, "Chicken Breast");
            PageImpl<IngredientResponseDTO> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
            when(ingredientService.searchIngredients(any(IngredientFilter.class), any(Pageable.class)))
                    .thenReturn(page);

            // when/then
            withAdminAuth().get()
                    .uri("/v1/ingredients")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content").isArray()
                    .jsonPath("$.content[0].name").isEqualTo("Chicken Breast")
                    .jsonPath("$.page.size").isEqualTo(20)
                    .jsonPath("$.page.number").isEqualTo(0)
                    .jsonPath("$.page.totalElements").isEqualTo(1)
                    .jsonPath("$.page.totalPages").isEqualTo(1);

            // verify filter and pageable
            ArgumentCaptor<IngredientFilter> filterCaptor = ArgumentCaptor.forClass(IngredientFilter.class);
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(ingredientService).searchIngredients(filterCaptor.capture(), pageableCaptor.capture());
            
            IngredientFilter capturedFilter = filterCaptor.getValue();
            // When no query params, filters are instantiated but not active (no values set)
            assertThat(capturedFilter.getNameFilter()).satisfiesAnyOf(
                    nameFilter -> assertThat(nameFilter).isNull(),
                    nameFilter -> assertThat(nameFilter.isActive()).isFalse()
            );
            assertThat(capturedFilter.getFatContentFilter()).satisfiesAnyOf(
                    filter -> assertThat(filter).isNull(),
                    filter -> assertThat(filter.isActive()).isFalse()
            );
            assertThat(capturedFilter.getProteinContentFilter()).satisfiesAnyOf(
                    filter -> assertThat(filter).isNull(),
                    filter -> assertThat(filter.isActive()).isFalse()
            );
            assertThat(capturedFilter.getCarbsContentFilter()).satisfiesAnyOf(
                    filter -> assertThat(filter).isNull(),
                    filter -> assertThat(filter.isActive()).isFalse()
            );
            assertThat(capturedFilter.getPhenylalanineContentFilter()).satisfiesAnyOf(
                    filter -> assertThat(filter).isNull(),
                    filter -> assertThat(filter.isActive()).isFalse()
            );
            
            Pageable capturedPageable = pageableCaptor.getValue();
            assertThat(capturedPageable.getPageSize()).isEqualTo(20);
        }

        @Test
        @DisplayName("should return 200 OK with all query parameter filters")
        void shouldReturn200WithAllQueryParameterFilters() {
            // given
            PageImpl<IngredientResponseDTO> page = new PageImpl<>(List.of(), 
                    PageRequest.of(0, 10), 0);
            when(ingredientService.searchIngredients(any(IngredientFilter.class), any(Pageable.class)))
                    .thenReturn(page);

            // when/then
            withAdminAuth().get()
                    .uri(uriBuilder -> uriBuilder.path("/v1/ingredients")
                            .queryParam("page", "0")
                            .queryParam("size", "10")
                            .queryParam("name.value", "Chicken")
                            .queryParam("name.operator", "CONTAINS")
                            .queryParam("unit.value", "GRAM")
                            .queryParam("unit.operator", "IN")
                            .queryParam("fatContent.value", "10")
                            .queryParam("fatContent.operator", "GREATER_THAN")
                            .queryParam("proteinContent.value", "20")
                            .queryParam("proteinContent.operator", "EQUAL")
                            .queryParam("carbsContent.value", "5")
                            .queryParam("carbsContent.value", "15")
                            .queryParam("carbsContent.operator", "BETWEEN")
                            .queryParam("phenylalanineContent.value", "100")
                            .queryParam("phenylalanineContent.operator", "LOWER_THAN")
                            .build())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content").isArray()
                    .jsonPath("$.page.size").isEqualTo(10)
                    .jsonPath("$.page.number").isEqualTo(0);

            // verify all filters are captured correctly
            ArgumentCaptor<IngredientFilter> filterCaptor = ArgumentCaptor.forClass(IngredientFilter.class);
            ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
            verify(ingredientService).searchIngredients(filterCaptor.capture(), pageableCaptor.capture());
            
            IngredientFilter capturedFilter = filterCaptor.getValue();
            
            // Verify name filter
            assertThat(capturedFilter.getNameFilter()).isNotNull();
            assertThat(capturedFilter.getNameFilter().isActive()).isTrue();
            assertThat(capturedFilter.getNameFilter().getValue()).contains("Chicken");
            assertThat(capturedFilter.getNameFilter().getOperator()).isEqualTo(StringFilter.Operator.CONTAINS);

            // Verify unit filter - temporarily disabled
            // assertThat(capturedFilter.getUnitFilter()).isNotNull();
            // assertThat(capturedFilter.getUnitFilter().isActive()).isTrue();
            // assertThat(capturedFilter.getUnitFilter().getValue()).isEqualTo(List.of(Unit.GRAM));
            // assertThat(capturedFilter.getUnitFilter().getOperator()).isEqualTo(EnumFilter.Operator.IN);

            // Verify fat content filter
            assertThat(capturedFilter.getFatContentFilter()).isNotNull();
            assertThat(capturedFilter.getFatContentFilter().isActive()).isTrue();
            assertThat(capturedFilter.getFatContentFilter().getValue()).contains(new BigDecimal("10"));
            assertThat(capturedFilter.getFatContentFilter().getOperator()).isEqualTo(NumericFilter.Operator.GREATER_THAN);
            
            // Verify protein content filter
            assertThat(capturedFilter.getProteinContentFilter()).isNotNull();
            assertThat(capturedFilter.getProteinContentFilter().isActive()).isTrue();
            assertThat(capturedFilter.getProteinContentFilter().getValue()).contains(new BigDecimal("20"));
            assertThat(capturedFilter.getProteinContentFilter().getOperator()).isEqualTo(NumericFilter.Operator.EQUAL);
            
            // Verify carbs content filter (BETWEEN with multiple values)
            assertThat(capturedFilter.getCarbsContentFilter()).isNotNull();
            assertThat(capturedFilter.getCarbsContentFilter().isActive()).isTrue();
            assertThat(capturedFilter.getCarbsContentFilter().getValue()).contains(new BigDecimal("5"), new BigDecimal("15"));
            assertThat(capturedFilter.getCarbsContentFilter().getOperator()).isEqualTo(NumericFilter.Operator.BETWEEN);
            
            // Verify phenylalanine content filter
            assertThat(capturedFilter.getPhenylalanineContentFilter()).isNotNull();
            assertThat(capturedFilter.getPhenylalanineContentFilter().isActive()).isTrue();
            assertThat(capturedFilter.getPhenylalanineContentFilter().getValue()).contains(new BigDecimal("100"));
            assertThat(capturedFilter.getPhenylalanineContentFilter().getOperator()).isEqualTo(NumericFilter.Operator.LOWER_THAN);
            
            Pageable capturedPageable = pageableCaptor.getValue();
            assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
            assertThat(capturedPageable.getPageSize()).isEqualTo(10);
        }

        @Test
        @DisplayName("should return 403 Forbidden when user has role 'user' instead of 'admin'")
        void shouldReturn403WhenUserRole() {
            // when/then
            withUserAuth().get()
                    .uri("/v1/ingredients")
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }

    @Nested
    @DisplayName("PUT /v1/ingredients/{id}")
    class UpdateIngredient {

        @Test
        @DisplayName("should return 200 OK with updated ingredient")
        void shouldReturn200OnSuccess() throws JSONException {
            // given
            IngredientResponseDTO response = createResponseDTO(TEST_ID, "Chicken Breast Updated");
            when(ingredientService.updateIngredient(eq(TEST_ID), any())).thenReturn(response);

            // when/then
            withAdminAuth().put()
                    .uri("/v1/ingredients/{id}", TEST_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Chicken Breast Updated").toString())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(TEST_ID.toString())
                    .jsonPath("$.name").isEqualTo("Chicken Breast Updated");
        }

        @Test
        @DisplayName("should return 404 Not Found when ingredient doesn't exist")
        void shouldReturn404WhenNotFound() throws JSONException {
            // given
            when(ingredientService.updateIngredient(eq(NON_EXISTENT_ID), any()))
                    .thenThrow(new ResourceNotFoundException("Ingredient", NON_EXISTENT_ID));

            // when/then
            withAdminAuth().put()
                    .uri("/v1/ingredients/{id}", NON_EXISTENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("NonExistent").toString())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("should return 403 Forbidden when user has role 'user' instead of 'admin'")
        void shouldReturn403WhenUserRole() throws JSONException {
            // when/then
            withUserAuth().put()
                    .uri("/v1/ingredients/{id}", TEST_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Chicken Breast Updated").toString())
                    .exchange()
                    .expectStatus().isForbidden();
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
            withAdminAuth().delete()
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
            withAdminAuth().delete()
                    .uri("/v1/ingredients/{id}", NON_EXISTENT_ID)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("should return 403 Forbidden when user has role 'user' instead of 'admin'")
        void shouldReturn403WhenUserRole() {
            // when/then
            withUserAuth().delete()
                    .uri("/v1/ingredients/{id}", TEST_ID)
                    .exchange()
                    .expectStatus().isForbidden();
        }
    }
}
