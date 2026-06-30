## Context

The backend codebase uses Spring Modulith with packages under `net.pvytykac.nutrition`. Some packages already have `package-info.java` with `@NullMarked`, but coverage is inconsistent:
- 6 packages are missing `package-info.java` entirely (root, 4 `internal/` subpackages, 1 `exceptions/internal` subpackage)
- `common.filtering` has a `package-info.java` with `@NamedInterface` but no `@NullMarked`
- JSpecify is not declared as a dependency in `pom.xml`
- Existing code has no systematic null-safety enforcement

## Goals / Non-Goals

**Goals:**
- Add `org.jspecify:jspecify` as a compile-time dependency
- Add NullAway via Error Prone as a build-time null-safety checker
- Disable all Error Prone checks except NullAway (only null safety issues are reported)
- Every package under `net.pvytykac.nutrition` in `src/main/java` has a `package-info.java` with `@NullMarked`
- Any compilation warnings/errors from the new annotations are fixed (e.g., missing null checks, improper nullable return types)

**Non-Goals:**
- No runtime behavior changes — this is a compile-time annotation only
- No changes to business logic, APIs, or data models
- No addition of `@Nullable`/`@NonNull` on individual elements yet (follow-up work)
- No other Error Prone checks beyond null safety

## Decisions

1. **JSpecify over javax.annotation or checkerframework** — JSpecify (`org.jspecify:jspecify`) is the standardized successor to the javax.annotation API, supported by IDE null-checking and static analysis tools. It's the recommended choice for new Java projects.
2. **Package-level `@NullMarked` over method/field annotations** — `@NullMarked` at the package level applies to all types in the package, avoiding repetition. Individual `@Nullable` annotations can be added later where null is explicitly allowed.
3. **`compile` scope (not `runtime` or `provided`)** — The annotation is retained in the class file but has no runtime behavior. `compile` scope matches Maven conventions for JSpecify.
4. **No `@NullMarked` on test sources** — Test packages are excluded to keep scope focused. Tests typically have different null contracts and adding annotations would add noise.
5. **NullAway over standalone checker** — NullAway integrates with Error Prone as a javac plugin, running during normal `mvn compile`. It uses JSpecify annotations natively and is fast (sub-second per compilation unit). It is the standard null-safety checker for Java projects using JSpecify.
6. **Disable all Error Prone checks except NullAway** — Error Prone bundles many checks beyond null safety (e.g., `EqualsInconsistent`, `StringSplitter`). Using `-XepDisableAllChecks` plus `-Xep:NullAway:ERROR` ensures only null safety issues are enforced, avoiding noise from unrelated checks.

## Risks / Trade-offs

- [Risk] New compilation errors may surface from previously-unannotated code → Mitigation: add `@Nullable` on parameters/returns where null is valid, or restructure code to avoid nullable contracts
- [Risk] Third-party library calls returning null may trigger warnings → Mitigation: wrap with `requireNonNull` or add explicit null checks
- [Trade-off] Package-level `@NullMarked` means all types in the package default to non-null, which is stricter than the current no-annotation state — this is the desired outcome
