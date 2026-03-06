package net.pvytykac.nutrition.ingredient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public IngredientResponseDTO getIngredientById(Long id) {
        log.debug("Fetching ingredient with id: {}", id);
        
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ingredient not found with id: {}", id);
                    return new IngredientNotFoundException(id);
                });
        
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

    public IngredientResponseDTO updateIngredient(Long id, IngredientRequestDTO request) {
        log.info("Updating ingredient with id: {}", id);
        
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Ingredient not found with id: {}", id);
                    return new IngredientNotFoundException(id);
                });
        
        ingredient.setName(request.getName());
        ingredient.setNutritionDetails(mapToNutritionDetails(request.getNutritionDetails()));
        
        Ingredient saved = ingredientRepository.save(ingredient);
        log.info("Updated ingredient with id: {}", saved.getId());
        
        return mapToResponseDTO(saved);
    }

    public void deleteIngredient(Long id) {
        log.info("Deleting ingredient with id: {}", id);
        
        if (!ingredientRepository.existsById(id)) {
            log.warn("Ingredient not found with id: {}", id);
            throw new IngredientNotFoundException(id);
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
