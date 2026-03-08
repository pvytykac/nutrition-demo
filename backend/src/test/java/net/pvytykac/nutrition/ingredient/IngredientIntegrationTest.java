package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.IntegrationTestBase;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("IngredientController Integration Test")
class IngredientIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("smoke test: POST -> GET all -> PUT -> GET /id -> DELETE")
    void shouldPerformCrudOperations() throws Exception {
        // given - request body
        JSONObject postRequest = new JSONObject()
                .put("name", "Test Ingredient")
                .put("quantity", 100.0)
                .put("unit", "GRAM")
                .put("nutritionDetails", new JSONObject()
                        .put("fatContent", 1.0)
                        .put("carbsContent", 2.0)
                        .put("proteinContent", 3.0)
                        .put("phenylalanineContent", 0.5)
                        .put("kilocalories", 50.0));

        // when - POST
        var postResponse = withAdminAuth().post()
                .uri("/v1/ingredients")
                .header("Content-Type", "application/json")
                .bodyValue(postRequest.toString())
                .exchange();

        // then - POST should return 201 Created
        postResponse.expectStatus().isCreated();
        String responseBody = new String(postResponse.returnResult(String.class).getResponseBodyContent());
        String id = new JSONObject(responseBody).getString("id");
        String baseUrl = "/v1/ingredients/" + id;

        // when - GET all
        var getAllResponse = withAdminAuth().get()
                .uri("/v1/ingredients")
                .exchange();

        // then - GET all should return 200 OK
        getAllResponse.expectStatus().isOk();
        getAllResponse.expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[?(@.name=='Test Ingredient')].name").isEqualTo("Test Ingredient");

        // given - update request body
        JSONObject putRequest = new JSONObject()
                .put("name", "Updated Ingredient")
                .put("quantity", 200.0)
                .put("unit", "MILILITER")
                .put("nutritionDetails", new JSONObject()
                        .put("fatContent", 5.0)
                        .put("carbsContent", 10.0)
                        .put("proteinContent", 15.0)
                        .put("phenylalanineContent", 2.5)
                        .put("kilocalories", 100.0));

        // when - PUT using baseUrl from POST
        var putResponse = withAdminAuth().put()
                .uri(baseUrl)
                .header("Content-Type", "application/json")
                .bodyValue(putRequest.toString())
                .exchange();

        // then - PUT should return 200 OK
        putResponse.expectStatus().isOk();

        // when - GET /id using baseUrl from POST
        var getByIdResponse = withAdminAuth().get()
                .uri(baseUrl)
                .exchange();

        // then - GET /id should return 200 OK with updated data
        getByIdResponse.expectStatus().isOk();
        getByIdResponse.expectBody()
                .jsonPath("$.name").isEqualTo("Updated Ingredient")
                .jsonPath("$.quantity").isEqualTo(200.0)
                .jsonPath("$.unit").isEqualTo("MILILITER");

        // when - DELETE
        var deleteResponse = withAdminAuth().delete()
                .uri(baseUrl)
                .exchange();

        // then - DELETE should return 204 No Content
        deleteResponse.expectStatus().isNoContent();

        // when - GET /id after delete should return 404
        var getAfterDelete = withAdminAuth().get()
                .uri(baseUrl)
                .exchange();

        getAfterDelete.expectStatus().isNotFound();
    }
}
