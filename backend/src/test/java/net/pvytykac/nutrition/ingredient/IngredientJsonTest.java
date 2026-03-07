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
                    .fat(new BigDecimal("10.5"))
                    .carbs(new BigDecimal("20.0"))
                    .protein(new BigDecimal("15.0"))
                    .phenylalanine(new BigDecimal("5.0"))
                    .unit("100g")
                    .build();

            IngredientRequestDTO request = IngredientRequestDTO.builder()
                    .name("Chicken Breast")
                    .nutritionDetails(nutritionDetails)
                    .build();

            // when
            JSONObject json = new JSONObject()
                .put("name", request.getName())
                .put("nutritionDetails", new JSONObject()
                    .put("fat", request.getNutritionDetails().getFat().doubleValue())
                    .put("carbs", request.getNutritionDetails().getCarbs().doubleValue())
                    .put("protein", request.getNutritionDetails().getProtein().doubleValue())
                    .put("phenylalanine", request.getNutritionDetails().getPhenylalanine().doubleValue())
                    .put("unit", request.getNutritionDetails().getUnit()));

            // then
            assertThat(json.get("name")).isEqualTo("Chicken Breast");
            assertThat(json.getJSONObject("nutritionDetails").getDouble("fat")).isEqualTo(10.5);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("carbs")).isEqualTo(20.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("protein")).isEqualTo(15.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("phenylalanine")).isEqualTo(5.0);
            assertThat(json.getJSONObject("nutritionDetails").get("unit")).isEqualTo("100g");
        }

        @Test
        @DisplayName("should deserialize request DTO correctly")
        void shouldDeserializeRequestDTO() throws JSONException {
            // given
            JSONObject json = new JSONObject(
                "{" +
                "\"name\": \"Apple\"," +
                "\"nutritionDetails\": {" +
                "\"fat\": 0.3," +
                "\"carbs\": 25.0," +
                "\"protein\": 0.5," +
                "\"phenylalanine\": 0.1," +
                "\"unit\": \"100g\"" +
                "}" +
                "}");

            // when
            IngredientRequestDTO request = IngredientRequestDTO.builder()
                    .name(json.getString("name"))
                    .nutritionDetails(NutritionDetailsRequestDTO.builder()
                            .fat(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("fat")))
                            .carbs(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("carbs")))
                            .protein(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("protein")))
                            .phenylalanine(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("phenylalanine")))
                            .unit(json.getJSONObject("nutritionDetails").getString("unit"))
                            .build())
                    .build();

            // then
            assertThat(request.getName()).isEqualTo("Apple");
            assertThat(request.getNutritionDetails()).isNotNull();
            assertThat(request.getNutritionDetails().getFat()).isEqualByComparingTo(new BigDecimal("0.3"));
            assertThat(request.getNutritionDetails().getCarbs()).isEqualByComparingTo(new BigDecimal("25.0"));
            assertThat(request.getNutritionDetails().getProtein()).isEqualByComparingTo(new BigDecimal("0.5"));
            assertThat(request.getNutritionDetails().getPhenylalanine()).isEqualByComparingTo(new BigDecimal("0.1"));
            assertThat(request.getNutritionDetails().getUnit()).isEqualTo("100g");
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
                    .fat(new BigDecimal("10.5"))
                    .carbs(new BigDecimal("20.0"))
                    .protein(new BigDecimal("15.0"))
                    .phenylalanine(new BigDecimal("5.0"))
                    .unit("100g")
                    .build();

            IngredientResponseDTO response = IngredientResponseDTO.builder()
                    .id(testId)
                    .name("Chicken Breast")
                    .nutritionDetails(nutritionDetails)
                    .build();

            // when
            JSONObject json = new JSONObject()
                .put("id", response.getId().toString())
                .put("name", response.getName())
                .put("nutritionDetails", new JSONObject()
                    .put("fat", response.getNutritionDetails().getFat().doubleValue())
                    .put("carbs", response.getNutritionDetails().getCarbs().doubleValue())
                    .put("protein", response.getNutritionDetails().getProtein().doubleValue())
                    .put("phenylalanine", response.getNutritionDetails().getPhenylalanine().doubleValue())
                    .put("unit", response.getNutritionDetails().getUnit()));

            // then
            assertThat(json.get("id")).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
            assertThat(json.get("name")).isEqualTo("Chicken Breast");
            assertThat(json.getJSONObject("nutritionDetails").getDouble("fat")).isEqualTo(10.5);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("carbs")).isEqualTo(20.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("protein")).isEqualTo(15.0);
            assertThat(json.getJSONObject("nutritionDetails").getDouble("phenylalanine")).isEqualTo(5.0);
            assertThat(json.getJSONObject("nutritionDetails").get("unit")).isEqualTo("100g");
        }

        @Test
        @DisplayName("should deserialize response DTO correctly")
        void shouldDeserializeResponseDTO() throws JSONException {
            // given
            JSONObject json = new JSONObject(
                "{" +
                "\"id\": \"550e8400-e29b-41d4-a716-446655440000\"," +
                "\"name\": \"Orange Juice\"," +
                "\"nutritionDetails\": {" +
                "\"fat\": 0.2," +
                "\"carbs\": 30.0," +
                "\"protein\": 1.0," +
                "\"phenylalanine\": 0.3," +
                "\"unit\": \"100ml\"" +
                "}" +
                "}");

            // when
            IngredientResponseDTO response = IngredientResponseDTO.builder()
                    .id(UUID.fromString(json.getString("id")))
                    .name(json.getString("name"))
                    .nutritionDetails(NutritionDetailsResponseDTO.builder()
                            .fat(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("fat")))
                            .carbs(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("carbs")))
                            .protein(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("protein")))
                            .phenylalanine(BigDecimal.valueOf(json.getJSONObject("nutritionDetails").getDouble("phenylalanine")))
                            .unit(json.getJSONObject("nutritionDetails").getString("unit"))
                            .build())
                    .build();

            // then
            assertThat(response.getId()).isEqualTo(UUID.fromString("550e8400-e29b-41d4-a716-446655440000"));
            assertThat(response.getName()).isEqualTo("Orange Juice");
            assertThat(response.getNutritionDetails()).isNotNull();
            assertThat(response.getNutritionDetails().getFat()).isEqualByComparingTo(new BigDecimal("0.2"));
            assertThat(response.getNutritionDetails().getCarbs()).isEqualByComparingTo(new BigDecimal("30.0"));
            assertThat(response.getNutritionDetails().getProtein()).isEqualByComparingTo(new BigDecimal("1.0"));
            assertThat(response.getNutritionDetails().getPhenylalanine()).isEqualByComparingTo(new BigDecimal("0.3"));
            assertThat(response.getNutritionDetails().getUnit()).isEqualTo("100ml");
        }
    }
}
