# utils

Kotlin Multiplatform shared utility library used by both the JVM backend and the JS frontend.

## Responsibilities

- Pure, stateless helper functions with no platform-specific dependencies
- Compiled to JVM (used by `domain` and `api`) and to JavaScript (used by `frontend`)

## Key facts

- Package root: `io.flavien.demo.utils`
- Targets: `jvm()` and `js(browser())`
- Source set: `commonMain` — all code must be platform-agnostic
- Forbidden: `java.*`, `kotlin.jvm.*`, browser APIs, Spring, JPA
- Only `kotlin.*` and `kotlinx.*` standard library APIs are allowed

## Build

```bash
./gradlew :utils:build --no-daemon
```
