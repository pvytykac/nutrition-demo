package net.pvytykac.nutrition.shared.exceptions;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Test controller used to trigger exceptions for testing GlobalExceptionHandler.
 * This controller is only loaded in test context.
 */
@RestController
@RequestMapping("/test-exceptions")
public class TestExceptionController {
    
    @GetMapping("/resource-not-found")
    public void throwResourceNotFound(
            @RequestParam String resourceType,
            @RequestParam String id) {
        throw new ResourceNotFoundException(resourceType, id);
    }
    
    @PostMapping("/validation-error")
    public String triggerValidationError(@Valid @RequestBody TestRequest request) {
        return "Success";
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestRequest {
        @NotBlank
        private String name;
        
        @NotNull
        @PositiveOrZero
        private Integer value;
    }
}
