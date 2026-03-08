package net.pvytykac.nutrition.recipe;

import net.pvytykac.nutrition.ControllerTestBase;
import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import net.pvytykac.nutrition.common.filtering.StringFilter;
import net.pvytykac.nutrition.ingredient.Unit;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(UserRecipeController.class)
@DisplayName("UserRecipeController")
class UserRecipeControllerTest extends ControllerTestBase {

    private static final UUID TEST_RECIPE_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
    private static final UUID TEST_INGREDIENT_ID = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
    private static final UUID NON_EXISTENT_ID = UUID.fromString("99999999-9999-9999-9999-999999999999");

    @MockitoBean
    private RecipeService recipeService;

    private RecipeResponseDTO createResponseDTO(UUID id, String name) {
        return RecipeResponseDTO.builder()
                .id(id)
                .name(name)
                .userId("test-user-id")
                .ingredients(List.of(
                        RecipeIngredientResponseDTO.builder()
                                .id(UUID.randomUUID())
                                .ingredientId(TEST_INGREDIENT_ID)
                                .ingredientName("Potato")
                                .baseQuantity(new BigDecimal("100.0"))
                                .unit(Unit.GRAM)
                                .multiplier(new BigDecimal("2.5"))
                                .calculatedQuantity(new BigDecimal("250.0"))
                                .fatContent(new BigDecimal("0.25"))
                                .carbsContent(new BigDecimal("42.5"))
                                .proteinContent(new BigDecimal("5.0"))
                                .phenylalanineContent(new BigDecimal("125.0"))
                                .kilocalories(new BigDecimal("192.5"))
                                .build()
                ))
                .build();
    }

    private JSONObject createRequestJSON(String name) throws JSONException {
        return new JSONObject()
                .put("name", name)
                .put("ingredients", new JSONArray()
                        .put(new JSONObject()
                                .put("ingredientId", TEST_INGREDIENT_ID.toString())
                                .put("multiplier", 2.5)));
    }

    private JSONObject createRequestJSONWithoutName() throws JSONException {
        return new JSONObject()
                .put("ingredients", new JSONArray()
                        .put(new JSONObject()
                                .put("ingredientId", TEST_INGREDIENT_ID.toString())
                                .put("multiplier", 2.5)));
    }

    private JSONObject createRequestJSONWithoutIngredients() throws JSONException {
        return new JSONObject()
                .put("name", "Test Recipe")
                .put("ingredients", new JSONArray());
    }

    @Nested
    @DisplayName("POST /v1/user/recipes")
    class CreateRecipe {

        @Test
        @DisplayName("should return 201 Created with recipe on success")
        void shouldReturn201CreatedWithRecipe() throws JSONException {
            // given
            RecipeResponseDTO response = createResponseDTO(TEST_RECIPE_ID, "Baked Potatoes");
            when(recipeService.createRecipe(any(), any())).thenReturn(response);

            // when/then
            withUserAuth().post()
                    .uri("/v1/user/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Baked Potatoes").toString())
                    .exchange()
                    .expectStatus().isCreated()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(TEST_RECIPE_ID.toString())
                    .jsonPath("$.name").isEqualTo("Baked Potatoes")
                    .jsonPath("$.ingredients[0].multiplier").isEqualTo(2.5)
                    .jsonPath("$.ingredients[0].calculatedQuantity").isEqualTo(250.0);
        }

        @Test
        @DisplayName("should return 400 Bad Request when name is missing")
        void shouldReturn400WhenNameMissing() throws JSONException {
            // when/then
            withUserAuth().post()
                    .uri("/v1/user/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSONWithoutName().toString())
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("should return 400 Bad Request when ingredients list is empty")
        void shouldReturn400WhenIngredientsEmpty() throws JSONException {
            // when/then
            withUserAuth().post()
                    .uri("/v1/user/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSONWithoutIngredients().toString())
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("should return 403 Forbidden when admin user tries to access user endpoint")
        void shouldReturn403WhenAdminUser() throws JSONException {
            // when/then
            withAdminAuth().post()
                    .uri("/v1/user/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Baked Potatoes").toString())
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @Test
        @DisplayName("should return 401 Unauthorized when no authentication")
        void shouldReturn401WhenNoAuth() throws JSONException {
            // when/then
            webTestClient.post()
                    .uri("/v1/user/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Baked Potatoes").toString())
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("GET /v1/user/recipes")
    class ListRecipes {

        @Test
        @DisplayName("should return 200 OK with paginated recipes")
        void shouldReturn200WithPaginatedRecipes() {
            // given
            RecipeResponseDTO response = createResponseDTO(TEST_RECIPE_ID, "Baked Potatoes");
            when(recipeService.searchRecipes(any(), any(), any()))
                    .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

            // when/then
            withUserAuth().get()
                    .uri("/v1/user/recipes")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content[0].id").isEqualTo(TEST_RECIPE_ID.toString())
                    .jsonPath("$.content[0].name").isEqualTo("Baked Potatoes");
        }

        @Test
        @DisplayName("should return 200 OK with empty page when no recipes")
        void shouldReturn200WithEmptyPage() {
            // given
            when(recipeService.searchRecipes(any(), any(), any()))
                    .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 20), 0));

            // when/then
            withUserAuth().get()
                    .uri("/v1/user/recipes")
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.content").isEmpty();
        }

        @Test
        @DisplayName("should pass name filter parameter to service")
        void shouldPassNameFilterToService() {
            // given
            when(recipeService.searchRecipes(any(), any(), any()))
                    .thenReturn(new PageImpl<>(Collections.emptyList()));

            // when
            withUserAuth().get()
                    .uri(builder -> builder.path("/v1/user/recipes")
                            .queryParam("name.value", "Baked Potatoes")
                            .queryParam("name.operator", "EXACT_MATCH")
                            .build())
                    .exchange()
                    .expectStatus().isOk();

            // then
            var captor = ArgumentCaptor.forClass(RecipeFilter.class);
            verify(recipeService).searchRecipes(any(), captor.capture(), any());

            assertThat(captor.getValue())
                    .isNotNull()
                    .doesNotReturn(null, RecipeFilter::getName)
                    .returns(Set.of("Baked Potatoes"), filter -> filter.getName().getValue())
                    .returns(StringFilter.Operator.EXACT_MATCH, filter -> filter.getName().getOperator());
        }

        @Test
        @DisplayName("should return 403 Forbidden when admin user tries to access user endpoint")
        void shouldReturn403WhenAdminUser() {
            // when/then
            withAdminAuth().get()
                    .uri("/v1/user/recipes")
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @Test
        @DisplayName("should return 401 Unauthorized when no authentication")
        void shouldReturn401WhenNoAuth() {
            // when/then
            webTestClient.get()
                    .uri("/v1/user/recipes")
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("GET /v1/user/recipes/{id}")
    class GetRecipeById {

        @Test
        @DisplayName("should return 200 OK with recipe when found")
        void shouldReturn200WhenFound() {
            // given
            RecipeResponseDTO response = createResponseDTO(TEST_RECIPE_ID, "Baked Potatoes");
            when(recipeService.getRecipeById(any(), eq(TEST_RECIPE_ID))).thenReturn(response);

            // when/then
            withUserAuth().get()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(TEST_RECIPE_ID.toString())
                    .jsonPath("$.name").isEqualTo("Baked Potatoes");
        }

        @Test
        @DisplayName("should return 404 Not Found when recipe not found")
        void shouldReturn404WhenNotFound() {
            // given
            when(recipeService.getRecipeById(any(), eq(NON_EXISTENT_ID)))
                    .thenThrow(new ResourceNotFoundException("Recipe", NON_EXISTENT_ID));

            // when/then
            withUserAuth().get()
                    .uri("/v1/user/recipes/{id}", NON_EXISTENT_ID)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("should return 403 Forbidden when admin user tries to access user endpoint")
        void shouldReturn403WhenAdminUser() {
            // when/then
            withAdminAuth().get()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @Test
        @DisplayName("should return 401 Unauthorized when no authentication")
        void shouldReturn401WhenNoAuth() {
            // when/then
            webTestClient.get()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("PUT /v1/user/recipes/{id}")
    class UpdateRecipe {

        @Test
        @DisplayName("should return 200 OK with updated recipe")
        void shouldReturn200WithUpdatedRecipe() throws JSONException {
            // given
            RecipeResponseDTO response = createResponseDTO(TEST_RECIPE_ID, "Updated Recipe Name");
            when(recipeService.updateRecipe(any(), eq(TEST_RECIPE_ID), any())).thenReturn(response);

            // when/then
            withUserAuth().put()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Updated Recipe Name").toString())
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody()
                    .jsonPath("$.id").isEqualTo(TEST_RECIPE_ID.toString())
                    .jsonPath("$.name").isEqualTo("Updated Recipe Name");
        }

        @Test
        @DisplayName("should return 404 Not Found when recipe not found")
        void shouldReturn404WhenNotFound() throws JSONException {
            // given
            when(recipeService.updateRecipe(any(), eq(NON_EXISTENT_ID), any()))
                    .thenThrow(new ResourceNotFoundException("Recipe", NON_EXISTENT_ID));

            // when/then
            withUserAuth().put()
                    .uri("/v1/user/recipes/{id}", NON_EXISTENT_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Updated Recipe").toString())
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("should return 400 Bad Request when name is missing")
        void shouldReturn400WhenNameMissing() throws JSONException {
            // when/then
            withUserAuth().put()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSONWithoutName().toString())
                    .exchange()
                    .expectStatus().isBadRequest();
        }

        @Test
        @DisplayName("should return 403 Forbidden when admin user tries to access user endpoint")
        void shouldReturn403WhenAdminUser() throws JSONException {
            // when/then
            withAdminAuth().put()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Updated Recipe").toString())
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @Test
        @DisplayName("should return 401 Unauthorized when no authentication")
        void shouldReturn401WhenNoAuth() throws JSONException {
            // when/then
            webTestClient.put()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(createRequestJSON("Updated Recipe").toString())
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }

    @Nested
    @DisplayName("DELETE /v1/user/recipes/{id}")
    class DeleteRecipe {

        @Test
        @DisplayName("should return 204 No Content when deleted successfully")
        void shouldReturn204WhenDeleted() {
            // given
            doNothing().when(recipeService).deleteRecipe(any(), eq(TEST_RECIPE_ID));

            // when/then
            withUserAuth().delete()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .exchange()
                    .expectStatus().isNoContent();
        }

        @Test
        @DisplayName("should return 404 Not Found when recipe not found")
        void shouldReturn404WhenNotFound() {
            // given
            doThrow(new ResourceNotFoundException("Recipe", NON_EXISTENT_ID))
                    .when(recipeService).deleteRecipe(any(), eq(NON_EXISTENT_ID));

            // when/then
            withUserAuth().delete()
                    .uri("/v1/user/recipes/{id}", NON_EXISTENT_ID)
                    .exchange()
                    .expectStatus().isNotFound();
        }

        @Test
        @DisplayName("should return 403 Forbidden when admin user tries to access user endpoint")
        void shouldReturn403WhenAdminUser() {
            // when/then
            withAdminAuth().delete()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .exchange()
                    .expectStatus().isForbidden();
        }

        @Test
        @DisplayName("should return 401 Unauthorized when no authentication")
        void shouldReturn401WhenNoAuth() {
            // when/then
            webTestClient.delete()
                    .uri("/v1/user/recipes/{id}", TEST_RECIPE_ID)
                    .exchange()
                    .expectStatus().isUnauthorized();
        }
    }
}
