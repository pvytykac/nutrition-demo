## Why

The codebase lacks consistent null-safety enforcement across packages. Several packages are missing `package-info.java` entirely, and some existing ones lack the `@NullMarked` annotation from JSpecify. This leads to ambiguity around nullable return values and parameters, increasing the risk of `NullPointerException` at runtime. Enforcing null safety via JSpecify `@NullMarked` at the package level makes null contracts explicit and catches violations at compile time.

## What Changes

- Add `org.jspecify:jspecify` as a compile-time dependency to `pom.xml`
- Add NullAway via Error Prone as a build-time null-safety checker in `maven-compiler-plugin`
- Disable all Error Prone checks except NullAway (only null safety issues are reported)
- Create `package-info.java` with `@NullMarked` for all main source packages that are missing it:
  - `net.pvytykac.nutrition` (root package)
  - `net.pvytykac.nutrition.common.exceptions.internal`
  - `net.pvytykac.nutrition.ingredient.internal`
  - `net.pvytykac.nutrition.nutrient.internal`
  - `net.pvytykac.nutrition.recipe.internal`
- Add `@NullMarked` to `net.pvytykac.nutrition.common.filtering` package-info.java (already has `@NamedInterface`)
- Fix any compilation warnings/errors introduced by the new null-safety constraints across all source files

## Capabilities

### New Capabilities
- `null-safety`: Package-level `@NullMarked` annotations across all modules to enforce compile-time null safety with JSpecify

### Modified Capabilities

*None — no existing specs are affected.*

## Impact

- **Dependencies**: Adds `org.jspecify:jspecify` (compile-time), `com.uber.nullaway:nullaway` (build-time), and `com.google.errorprone:error_prone_core` (build-time) to `pom.xml`
- **Source code**: 6 `package-info.java` files created or modified; potential minor null-safety fixes across the codebase
- **Build**: NullAway runs as a javac plugin during compilation, reporting null-safety violations as compilation errors. All other Error Prone checks are disabled.
- **No breaking changes**: Additive only — all existing behavior preserved
