package net.pvytykac.nutrition.recipe;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.pvytykac.nutrition.common.security.HasUserRole;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
@RequiredArgsConstructor
@RequestMapping("/v1/user/recipes")
@HasUserRole
@Tag(name = "User Recipes", description = "User recipe management endpoints")
class UserRecipeController {

    private final RecipeService recipeService;

    @PostMapping
    @Operation(summary = "Create a new recipe", description = "Creates a new recipe for the authenticated user")
    ResponseEntity<RecipeResponseDTO> createRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody RecipeRequestDTO request) {
        String userId = jwt.getSubject();
        log.info("Creating recipe '{}' for user: {}", request.getName(), userId);

        RecipeResponseDTO created = recipeService.createRecipe(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    @Operation(summary = "List user's recipes", description = "Returns all recipes belonging to the authenticated user with optional filtering")
    ResponseEntity<Page<RecipeResponseDTO>> listRecipes(
            @AuthenticationPrincipal Jwt jwt,
            @ParameterObject RecipesQueryParameters queryParameters,
            @ParameterObject Pageable pageable) {
        String userId = jwt.getSubject();
        log.debug("Listing recipes for user: {}", userId);

        RecipeFilter filter = RecipeFilter.builder()
                .name(queryParameters.getName())
                .build();

        Page<RecipeResponseDTO> recipes = recipeService.searchRecipes(userId, filter, pageable);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get recipe by ID", description = "Returns a specific recipe if owned by the authenticated user")
    ResponseEntity<RecipeResponseDTO> getRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        String userId = jwt.getSubject();
        log.debug("Fetching recipe {} for user: {}", id, userId);

        RecipeResponseDTO recipe = recipeService.getRecipeById(userId, id);
        return ResponseEntity.ok(recipe);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update recipe", description = "Updates a recipe if owned by the authenticated user")
    ResponseEntity<RecipeResponseDTO> updateRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id,
            @Valid @RequestBody RecipeRequestDTO request) {
        String userId = jwt.getSubject();
        log.info("Updating recipe {} for user: {}", id, userId);

        RecipeResponseDTO updated = recipeService.updateRecipe(userId, id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete recipe", description = "Deletes a recipe if owned by the authenticated user")
    ResponseEntity<Void> deleteRecipe(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID id) {
        String userId = jwt.getSubject();
        log.info("Deleting recipe {} for user: {}", id, userId);

        recipeService.deleteRecipe(userId, id);
        return ResponseEntity.noContent().build();
    }

}
