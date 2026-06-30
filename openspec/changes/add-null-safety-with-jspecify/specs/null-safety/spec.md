## ADDED Requirements

### Requirement: Package-level null safety
Every package under `net.pvytykac.nutrition` in `src/main/java` SHALL have a `package-info.java` file annotated with `@NullMarked`.

#### Scenario: Root package has @NullMarked
- **WHEN** building the project
- **THEN** `net.pvytykac.nutrition` package has a `package-info.java` with `@NullMarked`

#### Scenario: Internal subpackages have @NullMarked
- **WHEN** building the project
- **THEN** `net.pvytykac.nutrition.ingredient.internal`, `net.pvytykac.nutrition.nutrient.internal`, `net.pvytykac.nutrition.recipe.internal`, and `net.pvytykac.nutrition.common.exceptions.internal` each have a `package-info.java` with `@NullMarked`

### Requirement: Existing package-info files include @NullMarked
Every existing `package-info.java` without `@NullMarked` SHALL be updated to include it.

#### Scenario: common.filtering package gets @NullMarked
- **WHEN** inspecting `net.pvytykac.nutrition.common.filtering`'s `package-info.java`
- **THEN** it SHALL contain `@NullMarked` in addition to its existing `@NamedInterface` annotation

### Requirement: JSpecify dependency declared
The project SHALL declare `org.jspecify:jspecify` as a Maven dependency in `pom.xml` with `compile` scope.

#### Scenario: jspecify is in pom.xml
- **WHEN** running `mvn dependency:resolve`
- **THEN** `org.jspecify:jspecify` appears in the resolved dependency tree

### Requirement: NullAway enforcement at build time
The project SHALL run NullAway as a javac plugin during compilation to enforce null safety. All other Error Prone checks SHALL be disabled.

#### Scenario: NullAway configured in maven-compiler-plugin
- **WHEN** inspecting `pom.xml`
- **THEN** `maven-compiler-plugin` SHALL include `com.google.errorprone:error_prone_core` and `com.uber.nullaway:nullaway` in its annotation processor paths or compiler plugin configuration
- **AND** Error Prone SHALL be configured with `-XepDisableAllChecks` and `-Xep:NullAway:ERROR`

#### Scenario: Null safety violations fail the build
- **WHEN** code contains a nullable dereference in a `@NullMarked` package
- **THEN** compilation SHALL fail with a NullAway error

#### Scenario: Other Error Prone checks are silent
- **WHEN** code contains a non-null-safety Error Prone violation (e.g., inefficient string splitting)
- **THEN** compilation SHALL succeed (the violation is not reported)

### Requirement: Successful build with @NullMarked
- **WHEN** running `./mvnw clean compile`
- **THEN** the build completes successfully with no null-safety compilation errors
