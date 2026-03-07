package net.pvytykac.nutrition.ingredient;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Ingredient JSON Serialization")
class IngredientJsonTest {

    @Nested
    @DisplayName("IngredientRequestDTO")
    class IngredientRequestDTOTest {

        @Test
        @DisplayName("should serialize request DTO correctly")
        void shouldSerializeRequestDTO() throws JSONException {
            // given
            NutritionDetailsRequestDTO nutritionDetails = NutritionDetailsRequestDTO.builder()
                    .fatContent(new BigDecimal("10.5"))
                    .carbsContent(new BigDecimal("20.0"))
                    .proteinContent(new BigDecimal("15.0"))
                    .phenylalanineContent(new BigDecimal("5.0"))
                    .kilocalories(new BigDecimal("150.0"))
                    .build();

            IngredientRequestDTO request = IngredientRequestDTO.builder()
                    .name("Chicken Breast")
                    .quantity(new BigDecimal("100.0"))
                    .unit(Unit.GRAM)
                    .nutritionDetails(nutritionDetails)
                    .build();

            // when
            JSONObject json = new JSONObject()
                .put("name", request.getName())
                .put("quantity", request.getQuantity().doubleValue())
                .put("unit", request.getUnit().name())
                .put("nutritionDetails", new JSONObject()
                    .put("fatContent", request.getNutritionDetails().getFatContent().doubleValue())
                    .put("carbsContent", request.getNutritionDetails().getCarbsContent().doubleValue())
                    .put("proteinContent", request.getNutritionDetails().getProteinContent().doubleValue())
                    .put("phenylalanineContent", request.getNutritionDetails().getPhenylalanineContent().doubleValue())
                    .put("kilocalories", request.getNutritionDetails().getKilocalories().doubleValue()));

            // then
            assertThat(json.get("name")).isEqualTo("Chicken Breast");
            assertThat(json.getDouble("quantity")).isEqualTo(100.0);
            assertThat(json.get("unit")).isEqualTo("GRAM");
            assertThat(json.getJSONObject("nutritionDetails").getDouble("fatContent")).isEqualTo(10.5);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("carbsContent")).isEqualTo(20.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("proteinContent")).isEqualTo(15.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("phenylalanineContent")).isEqualTo(5.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("kilocalories")).isEqualTo(150.0);
        }

        @Test
        @DisplayName("should deserialize request DTO correctly")
        void shouldDeserializeRequestDTO() throws JSONException {
            // given
            JSONObject json = new JSONObject(
                "{" +
                "\"name\": \"Apple\"," +
                "\"quantity\": 100.0," +
                "\"unit\": \"GRAM\"," +
                "\"nutritionDetails\": {" +
                "\"fatContent\": 0.3," +
                "\"carbsContent\": 25.0," +
                "\"proteinContent\": 0.5," +
                "\"phenylalanineContent\": 0.1," +
                "\"kilocalories\": 50.0" +
                "}" +
                "}");

            // when
            IngredientRequestDTO request = IngredientRequestDTO.builder()
                    .name(json.getString("name"))
                    .quantity(BigDecimal.valueOf(json.getDouble("quantity")))
                    .unit(Unit.valueOf(json.getString("unit")))
                    .nutritionDetails(NutritionDetailsRequestDTO.builder()
                            .fatContent(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("fatContent")))
                            .carbsContent(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("carbsContent")))
                            .proteinContent(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("proteinContent")))
                            .phenylalanineContent(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("phenylalanineContent")))
                            .kilocalories(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("kilocalories")))
                            .build())
                    .build();

            // then
            assertThat(request.getName()).isEqualTo("Apple");
            assertThat(request.getQuantity()).isEqualByComparingTo(new BigDecimal("100.0"));
            assertThat(request.getUnit()).isEqualTo(Unit.GRAM);
            assertThat(request.getNutritionDetails()).isNotNull();
            assertThat(request.getNutritionDetails().getFatContent()).isEqualByComparingTo(new BigDecimal("0.3"));
            assertThat(request.getNutritionDetails().getCarbsContent()).isEqualByComparingTo(new BigDecimal("25.0"));
            assertThat(request.getNutritionDetails().getProteinContent()).isEqualByComparingTo(new BigDecimal("0.5"));
            assertThat(request.getNutritionDetails().getPhenylalanineContent()).isEqualByComparingTo(new BigDecimal("0.1"));
            assertThat(request.getNutritionDetails().getKilocalories()).isEqualByComparingTo(new BigDecimal("50.0"));
        }
    }

    @Nested
    @DisplayName("IngredientResponseDTO")
    class IngredientResponseDTOTest {

        @Test
        @DisplayName("should serialize response DTO correctly")
        void shouldSerializeResponseDTO() throws JSONException {
            // given
            UUID testId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
            NutritionDetailsResponseDTO nutritionDetails = NutritionDetailsResponseDTO.builder()
                    .fatContent(new BigDecimal("10.5"))
                    .carbsContent(new BigDecimal("20.0"))
                    .proteinContent(new BigDecimal("15.0"))
                    .phenylalanineContent(new BigDecimal("5.0"))
                    .kilocalories(new BigDecimal("150.0"))
                    .build();

            IngredientResponseDTO response = IngredientResponseDTO.builder()
                    .id(testId)
                    .name("Chicken Breast")
                    .quantity(new BigDecimal("100.0"))
                    .unit(Unit.GRAM)
                    .nutritionDetails(nutritionDetails)
                    .build();

            // when
            JSONObject json = new JSONObject()
                .put("id", response.getId().toString())
                .put("name", response.getName())
                .put("quantity", response.getQuantity().doubleValue())
                .put("unit", response.getUnit().name())
                .put("nutritionDetails", new JSONObject()
                    .put("fatContent", response.getNutritionDetails().getFatContent().doubleValue())
                    .put("carbsContent", response.getNutritionDetails().getCarbsContent().doubleValue())
                    .put("proteinContent", response.getNutritionDetails().getProteinContent().doubleValue())
                    .put("phenylalanineContent", response.getNutritionDetails().getPhenylalanineContent().doubleValue())
                    .put("kilocalories", response.getNutritionDetails().getKilocalories().doubleValue()));

            // then
            assertThat(json.get("id")).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
            assertThat(json.get("name")).isEqualTo("Chicken Breast");
            assertThat(json.getDouble("quantity")).isEqualTo(100.0);
            assertThat(json.get("unit")).isEqualTo("GRAM");
            assertThat(json.getJSONObject("nutritionDetails").getDouble("fatContent")).isEqualTo(10.5);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("carbsContent")).isEqualTo(20.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("proteinContent")).isEqualTo(15.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("phenylalanineContent")).isEqualTo(5.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("kilocalories")).isEqualTo(150.0);
        }

        @Test
        @DisplayName("should deserialize response DTO correctly")
        void shouldDeserializeResponseDTO() throws JSONException {
            // given
            JSONObject json = new JSONObject(
                "{" +
                "\"id\": \"550e8400-e29b-41d4-a716-446655440000\"," +
                "\"name\": \"Orange Juice\"," +
                "\"quantity\": 100.0," +
                "\"unit\": \"MILILITER\"," +
                "\"nutritionDetails\": {" +
                "\"fatContent\": 0.2," +
                "\"carbsContent\": 30.0," +
                "\"proteinContent\": 1.0," +
                "\"phenylalanineContent\": 0.3," +
                "\"kilocalories\": 45.0" +
                "}" +
                "}");

            // when
            IngredientResponseDTO response = IngredientResponseDTO.builder()
                    .id(UUID.fromString(json.getString("id")))
                    .name(json.getString("name"))
                    .quantity(BigDecimal.valueOf(json.getDouble("quantity")))
                    .unit(Unit.valueOf(json.getString("unit")))
                    .nutritionDetails(NutritionDetailsResponseDTO.builder()
                            .fatContent(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("fatContent")))
                            .carbsContent(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("carbsContent")))
                            .proteinContent(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("proteinContent")))
                            .phenylalanineContent(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("phenylalanineContent")))
                            .kilocalories(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("kilocalories")))
                            .build())
                    .build();

            // then
            assertThat(response.getId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
            assertThat(response.getName()).isEqualTo("Orange Juice");
            assertThat(response.getQuantity()).isEqualByComparingTo(new BigDecimal("100.0"));
            assertThat(response.getUnit()).isEqualTo(Unit.MILILITER);
            assertThat(response.getNutritionDetails()).isNotNull();
            assertThat(response.getNutritionDetails().getFatContent()).isEqualByComparingTo(new BigDecimal("0.2"));
            assertThat(response.getNutritionDetails().getCarbsContent()).isEqualByComparingTo(new BigDecimal("30.0"));
            assertThat(response.getNutritionDetails().getProteinContent()).isEqualByComparingTo(new BigDecimal("1.0"));
            assertThat(response.getNutritionDetails().getPhenylalanineContent()).isEqualByComparingTo(new BigDecimal("0.3"));
            assertThat(response.getNutritionDetails().getKilocalories()).isEqualByComparingTo(new BigDecimal("45.0"));
        }
    }
}
