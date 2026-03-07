package net.pvytykac.nutrition.ingredient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.nutrition.util.exceptions.ResourceNotFoundException;
import net.pvytykac.nutrition.util.filtering.NumberOperator;
import net.pvytykac.nutrition.util.filtering.StringOperator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientResponseDTO createIngredient(IngredientRequestDTO request) {
        log.info("Creating new ingredient: {}", request.getName());
        
        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .nutritionDetails(mapToNutritionDetails(request.getNutritionDetails()))
                .build();
        
        Ingredient saved = ingredientRepository.save(ingredient);
        log.info("Created ingredient with id: {}", saved.getId());
        
        return mapToResponseDTO(saved);
    }

    public IngredientResponseDTO getIngredientById(UUID id) {
        log.debug("Fetching ingredient with id: {}", id);
        
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
        
        return mapToResponseDTO(ingredient);
    }

    public List<IngredientResponseDTO> getAllIngredients() {
        log.debug("Fetching all ingredients");
        
        List<Ingredient> ingredients = ingredientRepository.findAll();
        log.debug("Found {} ingredients", ingredients.size());
        
        return ingredients.stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    public Page<IngredientResponseDTO> searchIngredients(
            String nameValue, 
            StringOperator nameOperator,
            BigDecimal phenylalanineValue, 
            BigDecimal phenylalanineSecondValue,
            NumberOperator phenylalanineOperator,
            Pageable pageable) {
        
        log.debug("Searching ingredients with filters - name: {}, phenylalanine: {}", nameValue, phenylalanineValue);
        
        List<Specification<Ingredient>> specs = new java.util.ArrayList<>();
        
        if (nameValue != null && !nameValue.isBlank()) {
            StringOperator op = nameOperator != null ? nameOperator : StringOperator.CONTAINS;
            specs.add(IngredientFilter.nameContains(nameValue, op));
        }
        
        if (phenylalanineValue != null) {
            NumberOperator op = phenylalanineOperator != null ? phenylalanineOperator : NumberOperator.EQUALS;
            specs.add(IngredientFilter.phenylalanineFilter(phenylalanineValue, phenylalanineSecondValue, op));
        }
        
        Specification<Ingredient> spec = specs.isEmpty() 
                ? null 
                : IngredientFilter.combine(specs);
        
        Page<Ingredient> ingredients = ingredientRepository.findAll(spec, pageable);
        log.debug("Found {} ingredients matching filters", ingredients.getTotalElements());
        
        return ingredients.map(this::mapToResponseDTO);
    }

    public IngredientResponseDTO updateIngredient(UUID id, IngredientRequestDTO request) {
        log.info("Updating ingredient with id: {}", id);
        
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
        
        ingredient.setName(request.getName());
        ingredient.setNutritionDetails(mapToNutritionDetails(request.getNutritionDetails()));
        
        Ingredient saved = ingredientRepository.save(ingredient);
        log.info("Updated ingredient with id: {}", saved.getId());
        
        return mapToResponseDTO(saved);
    }

    public void deleteIngredient(UUID id) {
        log.info("Deleting ingredient with id: {}", id);
        
        if (!ingredientRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ingredient", id);
        }
        
        ingredientRepository.deleteById(id);
        log.info("Deleted ingredient with id: {}", id);
    }

    private NutritionDetails mapToNutritionDetails(NutritionDetailsRequestDTO dto) {
        return NutritionDetails.builder()
                .fat(dto.getFat())
                .carbs(dto.getCarbs())
                .protein(dto.getProtein())
                .phenylalanine(dto.getPhenylalanine())
                .unit(dto.getUnit())
                .build();
    }

    private NutritionDetailsResponseDTO mapToNutritionDetailsResponse(NutritionDetails entity) {
        return NutritionDetailsResponseDTO.builder()
                .fat(entity.getFat())
                .carbs(entity.getCarbs())
                .protein(entity.getProtein())
                .phenylalanine(entity.getPhenylalanine())
                .unit(entity.getUnit())
                .build();
    }

    private IngredientResponseDTO mapToResponseDTO(Ingredient entity) {
        return IngredientResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .nutritionDetails(mapToNutritionDetailsResponse(entity.getNutritionDetails()))
                .build();
    }
}
