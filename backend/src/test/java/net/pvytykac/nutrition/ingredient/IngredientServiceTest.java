package net.pvytykac.nutrition.ingredient;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngredientService")
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    private IngredientRequestDTO createRequestDTO;
    private Ingredient createIngredient;
    private NutritionDetails createNutritionDetails;

    @BeforeEach
    void setUp() {
        NutritionDetailsRequestDTO nutritionRequest = NutritionDetailsRequestDTO.builder()
                .fat(new BigDecimal("10.5"))
                .carbs(new BigDecimal("20.0"))
                .protein(new BigDecimal("15.0"))
                .phenylalanine(new BigDecimal("5.0"))
                .unit("100g")
                .build();

        createRequestDTO = IngredientRequestDTO.builder()
                .name("Chicken Breast")
                .nutritionDetails(nutritionRequest)
                .build();

        createNutritionDetails = NutritionDetails.builder()
                .fat(new BigDecimal("10.5"))
                .carbs(new BigDecimal("20.0"))
                .protein(new BigDecimal("15.0"))
                .phenylalanine(new BigDecimal("5.0"))
                .unit("100g")
                .build();

        createIngredient = Ingredient.builder()
                .id(1L)
                .name("Chicken Breast")
                .nutritionDetails(createNutritionDetails)
                .build();
    }

    @Nested
    @DisplayName("createIngredient")
    class CreateIngredient {

        @Test
        @DisplayName("should create ingredient successfully")
        void shouldCreateIngredientSuccessfully() {
            // given
            when(ingredientRepository.save(any(Ingredient.class))).thenReturn(createIngredient);

            // when
            IngredientResponseDTO result = ingredientService.createIngredient(createRequestDTO);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Chicken Breast");
            assertThat(result.getNutritionDetails()).isNotNull();
            assertThat(result.getNutritionDetails().getFat()).isEqualByComparingTo(new BigDecimal("10.5"));
            assertThat(result.getNutritionDetails().getCarbs()).isEqualByComparingTo(new BigDecimal("20.0"));
            assertThat(result.getNutritionDetails().getProtein()).isEqualByComparingTo(new BigDecimal("15.0"));
            assertThat(result.getNutritionDetails().getPhenylalanine()).isEqualByComparingTo(new BigDecimal("5.0"));
            assertThat(result.getNutritionDetails().getUnit()).isEqualTo("100g");

            verify(ingredientRepository).save(any(Ingredient.class));
        }

        @Test
        @DisplayName("should save correct ingredient data")
        void shouldSaveCorrectIngredientData() {
            // given
            ArgumentCaptor<Ingredient> ingredientCaptor = ArgumentCaptor.forClass(Ingredient.class);
            when(ingredientRepository.save(ingredientCaptor.capture())).thenReturn(createIngredient);

            // when
            ingredientService.createIngredient(createRequestDTO);

            // then
            Ingredient savedIngredient = ingredientCaptor.getValue();
            assertThat(savedIngredient.getName()).isEqualTo("Chicken Breast");
            assertThat(savedIngredient.getNutritionDetails()).isNotNull();
            assertThat(savedIngredient.getNutritionDetails().getFat()).isEqualByComparingTo(new BigDecimal("10.5"));
            assertThat(savedIngredient.getNutritionDetails().getCarbs()).isEqualByComparingTo(new BigDecimal("20.0"));
            assertThat(savedIngredient.getNutritionDetails().getProtein()).isEqualByComparingTo(new BigDecimal("15.0"));
            assertThat(savedIngredient.getNutritionDetails().getPhenylalanine()).isEqualByComparingTo(new BigDecimal("5.0"));
            assertThat(savedIngredient.getNutritionDetails().getUnit()).isEqualTo("100g");
        }
    }

    @Nested
    @DisplayName("getIngredientById")
    class GetIngredientById {

        @Test
        @DisplayName("should return ingredient when found")
        void shouldReturnIngredientWhenFound() {
            // given
            when(ingredientRepository.findById(1L)).thenReturn(Optional.of(createIngredient));

            // when
            IngredientResponseDTO result = ingredientService.getIngredientById(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getName()).isEqualTo("Chicken Breast");
        }

        @Test
        @DisplayName("should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            // given
            when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> ingredientService.getIngredientById(999L))
                    .isInstanceOf(IngredientNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("getAllIngredients")
    class GetAllIngredients {

        @Test
        @DisplayName("should return all ingredients")
        void shouldReturnAllIngredients() {
            // given
            List<Ingredient> ingredients = List.of(createIngredient);
            when(ingredientRepository.findAll()).thenReturn(ingredients);

            // when
            List<IngredientResponseDTO> result = ingredientService.getAllIngredients();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("Chicken Breast");
        }

        @Test
        @DisplayName("should return empty list when no ingredients")
        void shouldReturnEmptyListWhenNoIngredients() {
            // given
            when(ingredientRepository.findAll()).thenReturn(List.of());

            // when
            List<IngredientResponseDTO> result = ingredientService.getAllIngredients();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateIngredient")
    class UpdateIngredient {

        @Test
        @DisplayName("should update ingredient successfully")
        void shouldUpdateIngredientSuccessfully() {
            // given
            when(ingredientRepository.findById(1L)).thenReturn(Optional.of(createIngredient));
            when(ingredientRepository.save(any(Ingredient.class))).thenReturn(createIngredient);

            // when
            IngredientResponseDTO result = ingredientService.updateIngredient(1L, createRequestDTO);

            // then
            assertThat(result).isNotNull();
            verify(ingredientRepository).save(any(Ingredient.class));
        }

        @Test
        @DisplayName("should throw exception when updating non-existent ingredient")
        void shouldThrowExceptionWhenUpdatingNonExistentIngredient() {
            // given
            when(ingredientRepository.findById(999L)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> ingredientService.updateIngredient(999L, createRequestDTO))
                    .isInstanceOf(IngredientNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteIngredient")
    class DeleteIngredient {

        @Test
        @DisplayName("should delete ingredient successfully")
        void shouldDeleteIngredientSuccessfully() {
            // given
            when(ingredientRepository.existsById(1L)).thenReturn(true);

            // when
            ingredientService.deleteIngredient(1L);

            // then
            verify(ingredientRepository).deleteById(1L);
        }

        @Test
        @DisplayName("should throw exception when deleting non-existent ingredient")
        void shouldThrowExceptionWhenDeletingNonExistentIngredient() {
            // given
            when(ingredientRepository.existsById(999L)).thenReturn(false);

            // when/then
            assertThatThrownBy(() -> ingredientService.deleteIngredient(999L))
                    .isInstanceOf(IngredientNotFoundException.class);
        }
    }
}
