package net.pvytykac.nutrition.recipe;

import net.pvytykac.nutrition.IntegrationTestBase;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("UserRecipeController Integration Test")
class RecipeIntegrationTest extends IntegrationTestBase {

    @Test
    @DisplayName("smoke test: POST ingredient -> POST recipe -> GET all -> GET /id -> PUT -> GET /id -> DELETE")
    void shouldPerformCrudOperations() throws Exception {
        // given - create ingredient first
        JSONObject ingredientRequest = new JSONObject()
                .put("name", "Test Potato")
                .put("quantity", 100.0)
                .put("unit", "GRAM")
                .put("nutritionDetails", new JSONObject()
                        .put("fatContent", 0.1)
                        .put("carbsContent", 17.0)
                        .put("proteinContent", 2.0)
                        .put("phenylalanineContent", 50.0)
                        .put("kilocalories", 77.0));

        var ingredientResponse = withAdminAuth().post()
                .uri("/v1/ingredients")
                .header("Content-Type", "application/json")
                .bodyValue(ingredientRequest.toString())
                .exchange();

        ingredientResponse.expectStatus().isCreated();
        String ingredientBody = new String(ingredientResponse.returnResult(String.class).getResponseBodyContent());
        String ingredientId = new JSONObject(ingredientBody).getString("id");

        // given - create recipe request
        JSONObject recipeRequest = new JSONObject()
                .put("name", "Baked Potatoes")
                .put("ingredients", new JSONArray()
                        .put(new JSONObject()
                                .put("ingredientId", ingredientId)
                                .put("multiplier", 2.5)));

        // when - POST recipe
        var postResponse = withUserAuth().post()
                .uri("/v1/user/recipes")
                .header("Content-Type", "application/json")
                .bodyValue(recipeRequest.toString())
                .exchange();

        // then - POST should return 201 Created with calculated values
        String responseBody = new String(postResponse.returnResult(String.class).getResponseBodyContent());
        String recipeId = new JSONObject(responseBody).getString("id");
        String baseUrl = "/v1/user/recipes/" + recipeId;

        // verify response contains expected data
        assert new JSONObject(responseBody).getString("name").equals("Baked Potatoes");
        assert new JSONObject(responseBody).getJSONArray("ingredients").getJSONObject(0).getDouble("multiplier") == 2.5;
        assert new JSONObject(responseBody).getJSONArray("ingredients").getJSONObject(0).getDouble("calculatedQuantity") == 250.0;

        // when - GET all
        var getAllResponse = withUserAuth().get()
                .uri("/v1/user/recipes")
                .exchange();

        // then - GET all should return 200 OK with recipe in list
        getAllResponse.expectStatus().isOk();
        getAllResponse.expectBody()
                .jsonPath("$.content").isArray()
                .jsonPath("$.content[?(@.name=='Baked Potatoes')].name").isEqualTo("Baked Potatoes");

        // when - GET by ID
        var getByIdResponse = withUserAuth().get()
                .uri(baseUrl)
                .exchange();

        // then - GET by ID should return 200 OK with recipe details
        getByIdResponse.expectStatus().isOk();
        getByIdResponse.expectBody()
                .jsonPath("$.id").isEqualTo(recipeId)
                .jsonPath("$.name").isEqualTo("Baked Potatoes")
                .jsonPath("$.ingredients[0].ingredientName").isEqualTo("Test Potato")
                .jsonPath("$.ingredients[0].baseQuantity").isEqualTo(100.0)
                .jsonPath("$.ingredients[0].multiplier").isEqualTo(2.5)
                .jsonPath("$.ingredients[0].calculatedQuantity").isEqualTo(250.0);

        // given - create second ingredient for update
        JSONObject butterRequest = new JSONObject()
                .put("name", "Test Butter")
                .put("quantity", 100.0)
                .put("unit", "GRAM")
                .put("nutritionDetails", new JSONObject()
                        .put("fatContent", 81.0)
                        .put("carbsContent", 0.1)
                        .put("proteinContent", 0.9)
                        .put("phenylalanineContent", 10.0)
                        .put("kilocalories", 717.0));

        var butterResponse = withAdminAuth().post()
                .uri("/v1/ingredients")
                .header("Content-Type", "application/json")
                .bodyValue(butterRequest.toString())
                .exchange();

        butterResponse.expectStatus().isCreated();
        String butterBody = new String(butterResponse.returnResult(String.class).getResponseBodyContent());
        String butterId = new JSONObject(butterBody).getString("id");

        // given - update request body
        JSONObject putRequest = new JSONObject()
                .put("name", "Mashed Potatoes")
                .put("ingredients", new JSONArray()
                        .put(new JSONObject()
                                .put("ingredientId", ingredientId)
                                .put("multiplier", 3.0))
                        .put(new JSONObject()
                                .put("ingredientId", butterId)
                                .put("multiplier", 0.5)));

        // when - PUT
        var putResponse = withUserAuth().put()
                .uri(baseUrl)
                .header("Content-Type", "application/json")
                .bodyValue(putRequest.toString())
                .exchange();

        // then - PUT should return 200 OK
        putResponse.expectStatus().isOk();
        putResponse.expectBody()
                .jsonPath("$.name").isEqualTo("Mashed Potatoes")
                .jsonPath("$.ingredients").isArray()
                .jsonPath("$.ingredients.length()").isEqualTo(2)
                .jsonPath("$.ingredients[0].multiplier").isEqualTo(3.0)
                .jsonPath("$.ingredients[0].calculatedQuantity").isEqualTo(300.0)
                .jsonPath("$.ingredients[1].multiplier").isEqualTo(0.5)
                .jsonPath("$.ingredients[1].calculatedQuantity").isEqualTo(50.0);

        // when - GET by ID after update
        var getAfterUpdate = withUserAuth().get()
                .uri(baseUrl)
                .exchange();

        // then - GET by ID should reflect updates
        getAfterUpdate.expectStatus().isOk();
        getAfterUpdate.expectBody()
                .jsonPath("$.name").isEqualTo("Mashed Potatoes")
                .jsonPath("$.ingredients[0].ingredientName").isEqualTo("Test Potato")
                .jsonPath("$.ingredients[1].ingredientName").isEqualTo("Test Butter");

        // when - DELETE
        var deleteResponse = withUserAuth().delete()
                .uri(baseUrl)
                .exchange();

        // then - DELETE should return 204 No Content
        deleteResponse.expectStatus().isNoContent();

        // when - GET by ID after delete should return 404
        var getAfterDelete = withUserAuth().get()
                .uri(baseUrl)
                .exchange();

        getAfterDelete.expectStatus().isNotFound();
    }

    @Test
    @DisplayName("should return 403 when admin tries to access user endpoints")
    void shouldReturn403ForAdminAccess() throws Exception {
        // given - create ingredient first
        JSONObject ingredientRequest = new JSONObject()
                .put("name", "Admin Test Ingredient")
                .put("quantity", 100.0)
                .put("unit", "GRAM")
                .put("nutritionDetails", new JSONObject()
                        .put("fatContent", 1.0)
                        .put("carbsContent", 10.0)
                        .put("proteinContent", 5.0)
                        .put("phenylalanineContent", 50.0)
                        .put("kilocalories", 100.0));

        var ingredientResponse = withAdminAuth().post()
                .uri("/v1/ingredients")
                .header("Content-Type", "application/json")
                .bodyValue(ingredientRequest.toString())
                .exchange();

        ingredientResponse.expectStatus().isCreated();
        String ingredientBody = new String(ingredientResponse.returnResult(String.class).getResponseBodyContent());
        String ingredientId = new JSONObject(ingredientBody).getString("id");

        // given - recipe request
        JSONObject recipeRequest = new JSONObject()
                .put("name", "Admin Test Recipe")
                .put("ingredients", new JSONArray()
                        .put(new JSONObject()
                                .put("ingredientId", ingredientId)
                                .put("multiplier", 1.0)));

        // when/then - admin should get 403 on all recipe endpoints
        withAdminAuth().post()
                .uri("/v1/user/recipes")
                .header("Content-Type", "application/json")
                .bodyValue(recipeRequest.toString())
                .exchange()
                .expectStatus().isForbidden();

        withAdminAuth().get()
                .uri("/v1/user/recipes")
                .exchange()
                .expectStatus().isForbidden();

        withAdminAuth().get()
                .uri("/v1/user/recipes/550e8400-e29b-41d4-a716-446655440000")
                .exchange()
                .expectStatus().isForbidden();

        withAdminAuth().put()
                .uri("/v1/user/recipes/550e8400-e29b-41d4-a716-446655440000")
                .header("Content-Type", "application/json")
                .bodyValue(recipeRequest.toString())
                .exchange()
                .expectStatus().isForbidden();

        withAdminAuth().delete()
                .uri("/v1/user/recipes/550e8400-e29b-41d4-a716-446655440000")
                .exchange()
                .expectStatus().isForbidden();
    }
}
