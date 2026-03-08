# Testing Guide

This document explains the testing strategy and patterns used in this project.

## Testing Pyramid

We use three main test types:

1. **Unit Tests** - Service logic, isolated with mocks
2. **Controller Tests** - HTTP layer with mocked services
3. **Repository Tests** - Database layer with Testcontainers PostgreSQL

## Test Base Classes

Three abstract base classes provide common configuration:

### ControllerTestBase

For testing Spring MVC controllers with `WebTestClient`.

```java
@WebMvcTest(IngredientController.class)
class IngredientControllerTest extends ControllerTestBase {
    @MockitoBean
    private IngredientService ingredientService;
    
    @Test
    void shouldReturn200WhenFound() {
        withAdminAuth().get()
            .uri("/v1/ingredients/{id}", testId)
            .exchange()
            .expectStatus().isOk();
    }
}
```

**Features:**
- Pre-configured `WebTestClient`
- Authentication helpers: `withAdminAuth()`, `withUserAuth()`
- Security configuration imported
- Use `@MockitoBean` to mock service layer

### RepositoryTestBase

For testing JPA repositories with PostgreSQL Testcontainer.

```java
class IngredientRepositoryTest extends RepositoryTestBase {
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Test
    void shouldSaveAndRetrieveIngredient() {
        // Test with real database
    }
}
```

**Features:**
- PostgreSQL 18 Testcontainer
- `TestEntityManager` for test data setup
- Liquibase disabled (uses Hibernate DDL)
- Real database semantics

### IntegrationTestBase

For full integration tests with complete Spring context.

## Test Naming Convention

Use `@Nested` classes grouped by the method under test, with descriptive test names:

```java
@Nested
@DisplayName("createIngredient")
class CreateIngredient {
    
    @Test
    @DisplayName("should create ingredient successfully")
    void shouldCreateIngredientSuccessfully() {
        // given
        // when
        // then
    }
    
    @Test
    @DisplayName("should throw exception when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        // given
        // when
        // then
    }
}
```

**Pattern:** `should{ExpectedBehavior}When{Condition}`

## Service Test Pattern

```java
@ExtendWith(MockitoExtension.class)
@DisplayName("IngredientService")
class IngredientServiceTest {
    
    @Mock
    private IngredientRepository ingredientRepository;
    
    @InjectMocks
    private IngredientService ingredientService;
    
    @Test
    void shouldCreateIngredientSuccessfully() {
        // given
        when(ingredientRepository.save(any())).thenReturn(savedIngredient);
        
        // when
        IngredientResponseDTO result = ingredientService.createIngredient(request);
        
        // then
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Chicken");
        verify(ingredientRepository).save(any());
    }
}
```

**Key Points:**
- Use `@ExtendWith(MockitoExtension.class)`
- Mock dependencies with `@Mock`
- Inject service with `@InjectMocks`
- Cover all branches including error cases
- Use AssertJ for assertions

## Controller Test Pattern

```java
@WebMvcTest(IngredientController.class)
@DisplayName("IngredientController")
class IngredientControllerTest extends ControllerTestBase {
    
    @MockitoBean
    private IngredientService ingredientService;
    
    @Test
    void shouldReturn201CreatedWithIngredient() {
        // given
        when(ingredientService.createIngredient(any())).thenReturn(response);
        
        // when/then
        withAdminAuth().post()
            .uri("/v1/ingredients")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isEqualTo(testId.toString())
            .jsonPath("$.name").isEqualTo("Chicken Breast");
    }
    
    @Test
    void shouldReturn403WhenUserRole() {
        withUserAuth().post()
            .uri("/v1/ingredients")
            .exchange()
            .expectStatus().isForbidden();
    }
}
```

**Key Points:**
- Extend `ControllerTestBase`
- Mock service with `@MockitoBean`
- Use `WebTestClient` for requests
- Test with both admin and user roles
- Verify response status codes and payloads
- Test validation errors

## Repository Test Pattern

```java
@DisplayName("IngredientRepository")
class IngredientRepositoryTest extends RepositoryTestBase {
    
    @Autowired
    private IngredientRepository ingredientRepository;
    
    @Test
    void shouldSaveAndRetrieveIngredient() {
        // given
        Ingredient ingredient = Ingredient.builder()
            .name("Test")
            .build();
        
        // when
        Ingredient saved = ingredientRepository.save(ingredient);
        
        // then
        assertThat(saved.getId()).isNotNull();
        
        // verify retrieval
        Optional<Ingredient> found = ingredientRepository.findById(saved.getId());
        assertThat(found).isPresent();
    }
}
```

**Key Points:**
- Extend `RepositoryTestBase`
- Use `TestEntityManager` for complex setups
- Test actual database behavior (constraints, queries)
- Test custom repository methods
- Test pessimistic locking if applicable

## Test Data Builders

Use builder pattern for test data:

```java
private IngredientResponseDTO createResponseDTO(UUID id, String name) {
    return IngredientResponseDTO.builder()
        .id(id)
        .name(name)
        .quantity(new BigDecimal("100.0"))
        .unit(Unit.GRAM)
        .nutritionDetails(createNutritionDetails())
        .build();
}
```

## Mocking Authentication

The `TestJwtDecoderConfig` creates mock JWTs based on token content:

- `mock-token-admin` → user has "admin" role
- `mock-token-user` → user has "user" role

Use `withAdminAuth()` or `withUserAuth()` from `ControllerTestBase`.

## Running Tests

```bash
# Run all tests
./mvnw test

# Run single test class
./mvnw test -Dtest=IngredientServiceTest

# Run single test method
./mvnw test -Dtest=IngredientServiceTest#shouldCreateIngredientSuccessfully

# Run tests matching pattern
./mvnw test -Dtest="*ControllerTest"
```

## Test Coverage

Target 100% line and branch coverage for:
- Service classes
- Controller classes
- Repository custom methods

Run coverage report:
```bash
./mvnw jacoco:report
```

## Testing Checklist

When writing tests, ensure you cover:

- [ ] Happy path
- [ ] Null/empty inputs
- [ ] Validation errors (400 Bad Request)
- [ ] Not found scenarios (404 Not Found)
- [ ] Authorization failures (403 Forbidden)
- [ ] Edge cases (boundary values, empty collections)
