package net.pvytykac.nutrition.ingredient;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.nutrition.common.security.HasAdminRole;
import net.pvytykac.nutrition.common.security.HasUserOrAdminRole;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/ingredients")
@RequiredArgsConstructor
@Tag(name = "Ingredients", description = "Ingredient management API")
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    @HasAdminRole
    @Operation(summary = "Create a new ingredient")
    public ResponseEntity<IngredientResponseDTO> createIngredient(@Valid @RequestBody IngredientRequestDTO request) {
        log.info("POST /v1/ingredients - Creating ingredient: {}", request.getName());
        IngredientResponseDTO created = ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @HasUserOrAdminRole
    @Operation(summary = "Get an ingredient by ID")
    public ResponseEntity<IngredientResponseDTO> getIngredient(@PathVariable UUID id) {
        log.debug("GET /v1/ingredients/{} - Fetching ingredient", id);
        IngredientResponseDTO ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }

    @GetMapping
    @HasUserOrAdminRole
    @Operation(summary = "Search ingredients with filtering and pagination")
    public ResponseEntity<Page<IngredientResponseDTO>> getAllIngredients(
            @ParameterObject IngredientsQueryParameters queryParameters,
            @ParameterObject Pageable pageable) {
        
        log.debug("GET /v1/ingredients - Fetching ingredients with filters and paging");
        
        var filter = IngredientFilter.builder()
            .nameFilter(queryParameters.getName())
            .unitFilter(queryParameters.getUnit())
            .fatContentFilter(queryParameters.getFatContent())
            .proteinContentFilter(queryParameters.getProteinContent())
            .carbsContentFilter(queryParameters.getCarbsContent())
            .phenylalanineContentFilter(queryParameters.getPhenylalanineContent())
            .build();
        
        Page<IngredientResponseDTO> ingredientsPage = ingredientService.searchIngredients(filter, pageable);
        
        return ResponseEntity.ok(ingredientsPage);
    }

    @PutMapping("/{id}")
    @HasAdminRole
    @Operation(summary = "Update an existing ingredient")
    public ResponseEntity<IngredientResponseDTO> updateIngredient(
            @PathVariable UUID id,
            @Valid @RequestBody IngredientRequestDTO request) {
        log.info("PUT /v1/ingredients/{} - Updating ingredient", id);
        IngredientResponseDTO updated = ingredientService.updateIngredient(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @HasAdminRole
    @Operation(summary = "Delete an ingredient")
    public ResponseEntity<Void> deleteIngredient(@PathVariable UUID id) {
        log.info("DELETE /v1/ingredients/{} - Deleting ingredient", id);
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
