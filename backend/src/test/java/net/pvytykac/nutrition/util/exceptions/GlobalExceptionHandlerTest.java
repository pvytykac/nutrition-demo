package net.pvytykac.nutrition.util.exceptions;

import net.pvytykac.nutrition.ControllerTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;

@WebMvcTest(controllers = {GlobalExceptionHandler.class, TestExceptionController.class})
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest extends ControllerTestBase {
    
    @Nested
    @DisplayName("ResourceNotFoundException handling")
    class ResourceNotFoundExceptionTests {
        
        @Test
        @DisplayName("should return 404 with correct ProblemDetail structure")
        void shouldReturn404WithProblemDetail() {
            // when/then
            webTestClient.get()
                    .uri("/test-exceptions/resource-not-found?resourceType=Recipe&id=123")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.title").isEqualTo("Not Found")
                    .jsonPath("$.detail").isEqualTo("Recipe not found with id: 123")
                    .jsonPath("$.instance").value(containsString("/test-exceptions/resource-not-found"))
                    .jsonPath("$.resourceType").isEqualTo("Recipe")
                    .jsonPath("$.resourceId").isEqualTo("123");
        }
        
        @Test
        @DisplayName("should handle Ingredient resource type")
        void shouldHandleIngredientResourceType() {
            // when/then
            webTestClient.get()
                    .uri("/test-exceptions/resource-not-found?resourceType=Ingredient&id=999")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.detail").isEqualTo("Ingredient not found with id: 999")
                    .jsonPath("$.resourceType").isEqualTo("Ingredient")
                    .jsonPath("$.resourceId").isEqualTo("999");
        }
        
        @Test
        @DisplayName("should handle Meal resource type")
        void shouldHandleMealResourceType() {
            // when/then
            webTestClient.get()
                    .uri("/test-exceptions/resource-not-found?resourceType=Meal&id=42")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.detail").isEqualTo("Meal not found with id: 42")
                    .jsonPath("$.resourceType").isEqualTo("Meal")
                    .jsonPath("$.resourceId").isEqualTo("42");
        }
        
        @Test
        @DisplayName("should include instance URI in response")
        void shouldIncludeInstanceUri() {
            // when/then
            webTestClient.get()
                    .uri("/test-exceptions/resource-not-found?resourceType=User&id=abc123")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.instance").exists()
                    .jsonPath("$.instance").value(containsString("/test-exceptions"));
        }
    }
    
    @Nested
    @DisplayName("Validation exception handling")
    class ValidationExceptionTests {
        
        @Test
        @DisplayName("should return 400 for validation errors")
        void shouldReturn400ForValidationErrors() {
            // given
            TestExceptionController.TestRequest invalidRequest = TestExceptionController.TestRequest.builder()
                    .name("")
                    .value(-1)
                    .build();
            
            // when/then
            webTestClient.post()
                    .uri("/test-exceptions/validation-error")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(400)
                    .jsonPath("$.title").isEqualTo("Bad Request")
                    .jsonPath("$.detail").isEqualTo("Invalid request content.")
                    .jsonPath("$.instance").value(containsString("/test-exceptions/validation-error"));
        }
        
        @Test
        @DisplayName("should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() {
            // given
            TestExceptionController.TestRequest invalidRequest = TestExceptionController.TestRequest.builder()
                    .name("   ")
                    .value(10)
                    .build();
            
            // when/then
            webTestClient.post()
                    .uri("/test-exceptions/validation-error")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(400);
        }
        
        @Test
        @DisplayName("should return 400 when value is negative")
        void shouldReturn400WhenValueIsNegative() {
            // given
            TestExceptionController.TestRequest invalidRequest = TestExceptionController.TestRequest.builder()
                    .name("test")
                    .value(-5)
                    .build();
            
            // when/then
            webTestClient.post()
                    .uri("/test-exceptions/validation-error")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(400);
        }
        
        @Test
        @DisplayName("should return 400 when value is null")
        void shouldReturn400WhenValueIsNull() {
            // given
            TestExceptionController.TestRequest invalidRequest = TestExceptionController.TestRequest.builder()
                    .name("test")
                    .value(null)
                    .build();
            
            // when/then
            webTestClient.post()
                    .uri("/test-exceptions/validation-error")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest();
        }
        
        @Test
        @DisplayName("should succeed with valid request")
        void shouldSucceedWithValidRequest() {
            // given
            TestExceptionController.TestRequest validRequest = TestExceptionController.TestRequest.builder()
                    .name("test")
                    .value(10)
                    .build();
            
            // when/then
            webTestClient.post()
                    .uri("/test-exceptions/validation-error")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validRequest)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .isEqualTo("Success");
        }
    }
}
