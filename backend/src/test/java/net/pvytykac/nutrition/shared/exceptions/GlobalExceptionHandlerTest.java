package net.pvytykac.nutrition.shared.exceptions;

import net.pvytykac.nutrition.shared.exceptions.TestExceptionController.TestRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webtestclient.autoconfigure.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {
    
    @Autowired
    private WebTestClient webTestClient;
    
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
            TestRequest invalidRequest = new TestRequest();
            invalidRequest.setName("");  // blank - invalid
            invalidRequest.setValue(-1);  // negative - invalid
            
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
                    .jsonPath("$.detail").isEqualTo("Validation failed")
                    .jsonPath("$.instance").value(containsString("/test-exceptions/validation-error"));
        }
        
        @Test
        @DisplayName("should return 400 when name is blank")
        void shouldReturn400WhenNameIsBlank() {
            // given
            TestRequest invalidRequest = new TestRequest();
            invalidRequest.setName("   ");  // blank
            invalidRequest.setValue(10);    // valid
            
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
            TestRequest invalidRequest = new TestRequest();
            invalidRequest.setName("test");
            invalidRequest.setValue(-5);
            
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
            TestRequest invalidRequest = new TestRequest();
            invalidRequest.setName("test");
            invalidRequest.setValue(null);
            
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
            TestRequest validRequest = new TestRequest();
            validRequest.setName("test");
            validRequest.setValue(10);
            
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
