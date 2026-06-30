## 1. Dependency Setup

- [x] 1.1 Add `org.jspecify:jspecify` as a `compile`-scope dependency in `backend/pom.xml`
- [x] 1.2 Add `com.google.errorprone:error_prone_core` and `com.uber.nullaway:nullaway` to `maven-compiler-plugin` compiler plugin dependencies in `backend/pom.xml`
- [x] 1.3 Configure Error Prone in `maven-compiler-plugin` with `-XepDisableAllChecks -Xep:NullAway:ERROR` to only enforce null safety

## 2. Create package-info.java for Missing Packages

- [x] 2.1 Create `package-info.java` with `@NullMarked` for `net.pvytykac.nutrition` (root package)
- [x] 2.2 Create `package-info.java` with `@NullMarked` for `net.pvytykac.nutrition.common.exceptions.internal`
- [x] 2.3 Create `package-info.java` with `@NullMarked` for `net.pvytykac.nutrition.ingredient.internal`
- [x] 2.4 Create `package-info.java` with `@NullMarked` for `net.pvytykac.nutrition.nutrient.internal`
- [x] 2.5 Create `package-info.java` with `@NullMarked` for `net.pvytykac.nutrition.recipe.internal`

## 3. Update Existing package-info.java

- [x] 3.1 Add `@NullMarked` to `net.pvytykac.nutrition.common.filtering`'s existing `package-info.java`

## 4. Compilation Fixes

- [x] 4.1 Run `./mvnw clean compile` and fix any null-safety compilation errors introduced by `@NullMarked` or NullAway
- [x] 4.2 Add `@Nullable` annotations on method parameters/return types where null is explicitly allowed by the API contract

## 5. Verification

- [x] 5.1 Run `./mvnw test` to confirm all existing tests pass with the new null-safety annotations
  - 86 of 99 tests pass. 13 errors in `SpecificationBuilderIntegrationTest` are pre-existing Testcontainers failures (Docker not running), unrelated to this change
