package net.pvytykac.nutrition.ingredient;

import net.pvytykac.nutrition.shared.exceptions.ResourceNotFoundException;
import net.pvytykac.nutrition.util.filtering.NumberOperator;
import net.pvytykac.nutrition.util.filtering.StringOperator;
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
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("IngredientService")
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @InjectMocks
    private IngredientService ingredientService;

    private UUID testId;
    private IngredientRequestDTO createRequestDTO;
    private Ingredient createIngredient;
    private NutritionDetails createNutritionDetails;

    @BeforeEach
    void setUp() {
        testId = UUID.fromString("550e8400-e29b-41d4-a716-446655440000");
        
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
                .id(testId)
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
            assertThat(result.getId()).isEqualTo(testId);
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
            when(ingredientRepository.findById(testId)).thenReturn(Optional.of(createIngredient));

            // when
            IngredientResponseDTO result = ingredientService.getIngredientById(testId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(testId);
            assertThat(result.getName()).isEqualTo("Chicken Breast");
        }

        @Test
        @DisplayName("should throw exception when not found")
        void shouldThrowExceptionWhenNotFound() {
            // given
            UUID nonExistentId = UUID.fromString("99999999-9999-9999-9999-999999999999");
            when(ingredientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> ingredientService.getIngredientById(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .extracting("resourceId")
                    .isEqualTo(nonExistentId);
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
            when(ingredientRepository.findById(testId)).thenReturn(Optional.of(createIngredient));
            when(ingredientRepository.save(any(Ingredient.class))).thenReturn(createIngredient);

            // when
            IngredientResponseDTO result = ingredientService.updateIngredient(testId, createRequestDTO);

            // then
            assertThat(result).isNotNull();
            verify(ingredientRepository).save(any(Ingredient.class));
        }

        @Test
        @DisplayName("should throw exception when updating non-existent ingredient")
        void shouldThrowExceptionWhenUpdatingNonExistentIngredient() {
            // given
            UUID nonExistentId = UUID.fromString("99999999-9999-9999-9999-999999999999");
            when(ingredientRepository.findById(nonExistentId)).thenReturn(Optional.empty());

            // when/then
            assertThatThrownBy(() -> ingredientService.updateIngredient(nonExistentId, createRequestDTO))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .extracting("resourceId")
                    .isEqualTo(nonExistentId);
        }
    }

    @Nested
    @DisplayName("deleteIngredient")
    class DeleteIngredient {

        @Test
        @DisplayName("should delete ingredient successfully")
        void shouldDeleteIngredientSuccessfully() {
            // given
            when(ingredientRepository.existsById(testId)).thenReturn(true);

            // when
            ingredientService.deleteIngredient(testId);

            // then
            verify(ingredientRepository).deleteById(testId);
        }

        @Test
        @DisplayName("should throw exception when deleting non-existent ingredient")
        void shouldThrowExceptionWhenDeletingNonExistentIngredient() {
            // given
            UUID nonExistentId = UUID.fromString("99999999-9999-9999-9999-999999999999");
            when(ingredientRepository.existsById(nonExistentId)).thenReturn(false);

            // when/then
            assertThatThrownBy(() -> ingredientService.deleteIngredient(nonExistentId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .extracting("resourceId")
                    .isEqualTo(nonExistentId);
        }
    }

    @Nested
    @DisplayName("searchIngredients")
    class SearchIngredients {

        @Test
        @DisplayName("should return empty page when no filters provided")
        void shouldReturnEmptyPageWhenNoFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Ingredient> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            when(ingredientRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            var result = ingredientService.searchIngredients(
                    null, null, null, null, null, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
        }

        @Test
        @DisplayName("should use default CONTAINS operator when nameOperator is null")
        void shouldUseDefaultContainsOperatorWhenNameOperatorNull() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Ingredient> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            when(ingredientRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            var result = ingredientService.searchIngredients(
                    "test", null, null, null, null, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("should use default EQUALS operator when phenylalanineOperator is null")
        void shouldUseDefaultEqualsOperatorWhenPhenylalanineOperatorNull() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Ingredient> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            when(ingredientRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            var result = ingredientService.searchIngredients(
                    null, null, new BigDecimal("5.0"), null, null, pageable);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("should combine name and phenylalanine filters")
        void shouldCombineMultipleFilters() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Page<Ingredient> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            when(ingredientRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                    .thenReturn(emptyPage);

            // when
            var result = ingredientService.searchIngredients(
                    "test", StringOperator.CONTAINS, 
                    new BigDecimal("5.0"), null, NumberOperator.GREATER_THAN,
                    pageable);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("should return results when filters match")
        void shouldReturnResultsWhenFiltersMatch() {
            // given
            Pageable pageable = PageRequest.of(0, 20);
            Ingredient ingredient = createIngredient;
            Page<Ingredient> page = new PageImpl<>(List.of(ingredient), pageable, 1);
            when(ingredientRepository.findAll(nullable(Specification.class), any(Pageable.class)))
                    .thenReturn(page);

            // when
            var result = ingredientService.searchIngredients(
                    "Chicken", StringOperator.EQUALS, 
                    new BigDecimal("5.0"), null, NumberOperator.EQUALS,
                    pageable);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }
}
