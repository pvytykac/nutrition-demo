package net.pvytykac.nutrition.ingredient;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.nutrition.util.filtering.NumberOperator;
import net.pvytykac.nutrition.util.filtering.StringOperator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private static final int DEFAULT_PAGE_SIZE = 20;

    private final IngredientService ingredientService;

    @PostMapping
    public ResponseEntity<IngredientResponseDTO> createIngredient(@Valid @RequestBody IngredientRequestDTO request) {
        log.info("POST /v1/ingredients - Creating ingredient: {}", request.getName());
        IngredientResponseDTO created = ingredientService.createIngredient(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientResponseDTO> getIngredient(@PathVariable Long id) {
        log.debug("GET /v1/ingredients/{} - Fetching ingredient", id);
        IngredientResponseDTO ingredient = ingredientService.getIngredientById(id);
        return ResponseEntity.ok(ingredient);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllIngredients(
            @RequestParam(required = false) String nameValue,
            @RequestParam(required = false) String nameOperator,
            @RequestParam(required = false) BigDecimal phenylalanineValue,
            @RequestParam(required = false) BigDecimal phenylalanineSecondValue,
            @RequestParam(required = false) String phenylalanineOperator,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        log.debug("GET /v1/ingredients - Fetching ingredients with filters and paging");
        
        size = Math.min(size, DEFAULT_PAGE_SIZE);
        Pageable pageable = PageRequest.of(page, size);
        
        StringOperator nameOp = nameOperator != null ? StringOperator.valueOf(nameOperator) : null;
        NumberOperator phenylalanineOp = phenylalanineOperator != null ? NumberOperator.valueOf(phenylalanineOperator) : null;
        
        Page<IngredientResponseDTO> ingredientsPage = ingredientService.searchIngredients(
                nameValue, 
                nameOp,
                phenylalanineValue,
                phenylalanineSecondValue,
                phenylalanineOp,
                pageable);
        
        return ResponseEntity.ok(Map.of(
                "content", ingredientsPage.getContent(),
                "totalElements", ingredientsPage.getTotalElements(),
                "totalPages", ingredientsPage.getTotalPages(),
                "pageNumber", ingredientsPage.getNumber(),
                "pageSize", ingredientsPage.getSize()
        ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientResponseDTO> updateIngredient(
            @PathVariable Long id,
            @Valid @RequestBody IngredientRequestDTO request) {
        log.info("PUT /v1/ingredients/{} - Updating ingredient", id);
        IngredientResponseDTO updated = ingredientService.updateIngredient(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        log.info("DELETE /v1/ingredients/{} - Deleting ingredient", id);
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }
}
