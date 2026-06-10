# library-test

Shared ArchUnit test infrastructure for Kotlin+Spring modules.

## Purpose

Provides `SpringModuleArchitectureTest`, an abstract class that carries the four
architecture rules common to every Kotlin+Spring module in this project:

- Logger fields must be `private static final` and named `log`
- No access to standard streams (`System.out` / `System.err` / `printStackTrace`)
- No field injection (constructor injection only)
- No `java.util.logging` usage (slf4j only)

## Usage

```kotlin
@AnalyzeClasses(
    packages = ["io.flavien.demo.yourmodule"],
    importOptions = [ImportOption.DoNotIncludeTests::class],
)
class ArchitectureTest : SpringModuleArchitectureTest() {
    // module-specific rules here
}
```

## Build commands

```bash
./gradlew :lib:lib-test:build   # compile
```
