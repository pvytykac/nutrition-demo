package net.pvytykac.nutrition.ingredient;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Ingredient JSON Serialization")
class IngredientJsonTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Nested
    @DisplayName("IngredientRequestDTO")
    class IngredientRequestDTOTest {

        @Test
        @DisplayName("should serialize request DTO correctly")
        void shouldSerializeRequestDTO() throws Exception {
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
            String json = objectMapper.writeValueAsString(request);

            // then
            assertThat(json).contains("\"name\":\"Chicken Breast\"");
            assertThat(json).contains("\"fat\":10.5");
            assertThat(json).contains("\"carbs\":20.0");
            assertThat(json).contains("\"protein\":15.0");
            assertThat(json).contains("\"phenylalanine\":5.0");
            assertThat(json).contains("\"unit\":\"100g\"");
        }

        @Test
        @DisplayName("should deserialize request DTO correctly")
        void shouldDeserializeRequestDTO() throws Exception {
            // given
            String json = """
                    {
                        "name": "Apple",
                        "nutritionDetails": {
                            "fat": 0.3,
                            "carbs": 25.0,
                            "protein": 0.5,
                            "phenylalanine": 0.1,
                            "unit": "100g"
                        }
                    }
                    """;

            // when
            IngredientRequestDTO request = objectMapper.readValue(json, IngredientRequestDTO.class);

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
        void shouldSerializeResponseDTO() throws Exception {
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
            String json = objectMapper.writeValueAsString(response);

            // then
            assertThat(json).contains("\"id\":\"550e8400-e29b-41d4-a716-446655440000\"");
            assertThat(json).contains("\"name\":\"Chicken Breast\"");
            assertThat(json).contains("\"fat\":10.5");
            assertThat(json).contains("\"carbs\":20.0");
            assertThat(json).contains("\"protein\":15.0");
            assertThat(json).contains("\"phenylalanine\":5.0");
            assertThat(json).contains("\"unit\":\"100g\"");
        }

        @Test
        @DisplayName("should deserialize response DTO correctly")
        void shouldDeserializeResponseDTO() throws Exception {
            // given
            String json = """
                    {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "name": "Orange Juice",
                        "nutritionDetails": {
                            "fat": 0.2,
                            "carbs": 30.0,
                            "protein": 1.0,
                            "phenylalanine": 0.3,
                            "unit": "100ml"
                        }
                    }
                    """;

            // when
            IngredientResponseDTO response = objectMapper.readValue(json, IngredientResponseDTO.class);

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
