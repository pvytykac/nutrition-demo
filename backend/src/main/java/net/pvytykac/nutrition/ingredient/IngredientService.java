package net.pvytykac.nutrition.ingredient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.nutrition.common.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class IngredientService {

    private final IngredientRepository ingredientRepository;

    public IngredientResponseDTO createIngredient(IngredientRequestDTO request) {
        log.info("Creating new ingredient: {}", request.getName());
        
        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .quantity(request.getQuantity())
                .unit(request.getUnit())
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

    public Page<IngredientResponseDTO> searchIngredients(IngredientFilter filter, Pageable pageable) {
        
        log.debug("Searching ingredients with filters");
        
        Specification<Ingredient> spec = filter.toSpecification();
        
        Page<Ingredient> ingredients = ingredientRepository.findAll(spec, pageable);
        log.debug("Found {} ingredients matching filters", ingredients.getTotalElements());
        
        return ingredients.map(this::mapToResponseDTO);
    }

    public IngredientResponseDTO updateIngredient(UUID id, IngredientRequestDTO request) {
        log.info("Updating ingredient with id: {}", id);
        
        Ingredient ingredient = ingredientRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
        
        ingredient.setName(request.getName());
        ingredient.setQuantity(request.getQuantity());
        ingredient.setUnit(request.getUnit());
        ingredient.setNutritionDetails(mapToNutritionDetails(request.getNutritionDetails()));
        
        Ingredient saved = ingredientRepository.save(ingredient);
        log.info("Updated ingredient with id: {}", saved.getId());
        
        return mapToResponseDTO(saved);
    }

    public void deleteIngredient(UUID id) {
        log.info("Deleting ingredient with id: {}", id);
        
        Ingredient ingredient = ingredientRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingredient", id));
        
        ingredientRepository.delete(ingredient);
        log.info("Deleted ingredient with id: {}", id);
    }

    private NutritionDetails mapToNutritionDetails(NutritionDetailsRequestDTO dto) {
        return NutritionDetails.builder()
                .fatContent(dto.getFatContent())
                .carbsContent(dto.getCarbsContent())
                .proteinContent(dto.getProteinContent())
                .phenylalanineContent(dto.getPhenylalanineContent())
                .kilocalories(dto.getKilocalories())
                .build();
    }

    private NutritionDetailsResponseDTO mapToNutritionDetailsResponse(NutritionDetails entity) {
        return NutritionDetailsResponseDTO.builder()
                .fatContent(entity.getFatContent())
                .carbsContent(entity.getCarbsContent())
                .proteinContent(entity.getProteinContent())
                .phenylalanineContent(entity.getPhenylalanineContent())
                .kilocalories(entity.getKilocalories())
                .build();
    }

    private IngredientResponseDTO mapToResponseDTO(Ingredient entity) {
        return IngredientResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .quantity(entity.getQuantity())
                .unit(entity.getUnit())
                .nutritionDetails(mapToNutritionDetailsResponse(entity.getNutritionDetails()))
                .build();
    }
}
