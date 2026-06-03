package net.pvytykac.nutrition.common.exceptions;

import net.pvytykac.nutrition.common.ControllerTestBase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;

@WebMvcTest(controllers = TestExceptionController.class)
@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest extends ControllerTestBase {

    @Nested
    @DisplayName("ResourceNotFoundException handling")
    class ResourceNotFoundExceptionTests {

        @Test
        @DisplayName("should return 404 with correct ProblemDetail structure")
        void shouldReturn404WithProblemDetail() {
            // when/then
            getRestHelper()
                    .unauthenticated()
                    .get()
                    .uri("/public/v1/test-exceptions/resource-not-found?resourceType=Recipe&id=123")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(404)
                    .jsonPath("$.title").isEqualTo("Not Found")
                    .jsonPath("$.detail").isEqualTo("Recipe not found with id: 123")
                    .jsonPath("$.instance").value(val -> containsString("/public/v1/test-exceptions/resource-not-found").matches(val))
                    .jsonPath("$.resourceType").isEqualTo("Recipe")
                    .jsonPath("$.resourceId").isEqualTo("123");
        }

        @Test
        @DisplayName("should handle Ingredient resource type")
        void shouldHandleIngredientResourceType() {
            // when/then
            getRestHelper()
                    .unauthenticated()
                    .get()
                    .uri("/public/v1/test-exceptions/resource-not-found?resourceType=Ingredient&id=999")
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
            getRestHelper()
                    .unauthenticated()
                    .get()
                    .uri("/public/v1/test-exceptions/resource-not-found?resourceType=Meal&id=42")
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
            getRestHelper().unauthenticated()
                    .get()
                    .uri("/public/v1/test-exceptions/resource-not-found?resourceType=User&id=abc123")
                    .exchange()
                    .expectStatus().isNotFound()
                    .expectBody()
                    .jsonPath("$.instance").exists()
                    .jsonPath("$.instance").value(val -> containsString("/public/v1/test-exceptions").matches(val));
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
            getRestHelper().unauthenticated()
                    .post()
                    .uri("/public/v1/test-exceptions/validation-error")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(invalidRequest)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(400)
                    .jsonPath("$.title").isEqualTo("Bad Request")
                    .jsonPath("$.detail").isEqualTo("Invalid request content.")
                    .jsonPath("$.instance").value(val -> containsString("/public/v1/test-exceptions/validation-error").matches(val));
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
            getRestHelper()
                    .unauthenticated()
                    .post()
                    .uri("/public/v1/test-exceptions/validation-error")
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
            getRestHelper().unauthenticated()
                    .post()
                    .uri("/public/v1/test-exceptions/validation-error")
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
            getRestHelper().unauthenticated()
                    .post()
                    .uri("/public/v1/test-exceptions/validation-error")
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
            getRestHelper().unauthenticated()
                    .post()
                    .uri("/public/v1/test-exceptions/validation-error")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(validRequest)
                    .exchange()
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .isEqualTo("Success");
        }
    }

    @Nested
    @DisplayName("Data integrity violation exception handling")
    class DataIntegrityViolationExceptionTests {

        @Test
        @DisplayName("should return 409 Conflict for DataIntegrityViolationException")
        void shouldReturn409ForDataIntegrityViolationException() {
            // when/then
            getRestHelper().unauthenticated()
                    .get()
                    .uri("/public/v1/test-exceptions/data-integrity-violation-exception")
                    .exchange()
                    .expectStatus().isEqualTo(409)
                    .expectBody()
                    .jsonPath("$.status").isEqualTo(409)
                    .jsonPath("$.title").isEqualTo("Conflict")
                    .jsonPath("$.detail").isEqualTo("A data integrity violation exception occurred")
                    .jsonPath("$.instance").value(val -> containsString("/public/v1/test-exceptions/data-integrity-violation-exception").matches(val));
        }
    }
}
