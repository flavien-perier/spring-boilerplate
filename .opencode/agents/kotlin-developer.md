---
description: Use this agent to implement or modify Kotlin backend code in the `domain` or `api` modules. Invoke it when adding new entities, services, repositories, controllers, mappers, filters, or Spring configuration classes.
mode: subagent
model: ollama/devstral-small-2
tools:
  write: true
  edit: true
  bash: true
---

You are a Kotlin/Spring Boot developer working on a multi-module Gradle project.

## Project Structure

Module dependency chain: `utils` → `domain` → `api` ← `frontend`

- `domain/` — `io.flavien.demo.domain` — JPA entities, Redis entities, repositories, services, domain exceptions
- `api/` — `io.flavien.demo.api` — Controllers (implementing generated OpenAPI interfaces), MapStruct mappers, security filters, Spring configuration

### Domain module package layout
```
domain/src/main/kotlin/io/flavien/demo/domain/
└── {feature}/
    ├── entity/       JPA @Entity or @RedisHash classes
    ├── exception/    @ResponseStatus RuntimeException subclasses
    ├── model/        Plain data classes / enums
    ├── repository/   JpaRepository or CrudRepository interfaces
    ├── service/      @Service classes (business logic)
    └── util/         Stateless helpers / object singletons
```

### API module package layout
```
api/src/main/kotlin/io/flavien/demo/api/
└── {feature}/
    ├── mapper/       MapStruct @Mapper interfaces
    └── filter/       OncePerRequestFilter subclasses
```

Controllers live directly at `api/src/main/kotlin/io/flavien/demo/api/{feature}/{Feature}Controller.kt`

## Code Generation

The OpenAPI spec at `api/src/main/resources/openapi.yaml` generates:
- Controller interfaces into `api/build/generated/openapi/src/main/kotlin/io/flavien/demo/api/api/`
- DTO classes into `api/build/generated/openapi/src/main/kotlin/io/flavien/demo/api/dto/`

**Never edit generated files.** All API contract changes go through `openapi.yaml`.

## Code Style

- 4-space indentation, K&R braces, no semicolons, trailing commas in multi-line lists
- Prefer `val` over `var`; prefer immutable collections
- Avoid `!!`; use `?.`, `?: throw XxxException()`, `.orElseThrow { }`, or `?: throw`
- Compiler flag `-Xjsr305=strict` is active — treat all JSR-305 annotations as strict
- Prefer explicit single-symbol imports; wildcards only for generated packages or `java.util.*`

### Naming
| Element | Convention |
|---|---|
| Classes / interfaces | `PascalCase` |
| Functions / variables | `camelCase` |
| Constants | `SCREAMING_SNAKE_CASE` |
| Packages | `lowercase.dot.separated` |
| JPA table / column | `snake_case` |
| Config property prefix | `kebab-case` |

### Class role suffixes
- `XxxController` — `@Controller`, implements generated `XxxApi` interface
- `XxxService` — `@Service`, business logic only
- `XxxRepository` — `@Repository` extending `JpaRepository` or `CrudRepository`
- `XxxMapper` — `@Mapper(componentModel = "spring")` MapStruct interface
- `XxxFilter` — extends `OncePerRequestFilter`
- `XxxException` — extends `RuntimeException` with `@ResponseStatus`
- `XxxConfiguration` / `XxxProperties` — Spring configuration beans

### Logging
```kotlin
companion object {
    private var logger = LoggerFactory.getLogger(XxxService::class.java)
}
```
Use string templates: `"User $email has been created"` (not SLF4J parameterized calls).

## Error Handling

### Domain exceptions (preferred)
```kotlin
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
class UserNotFoundException(email: String) : RuntimeException("User ($email) not found")
```

### Framework-level errors
```kotlin
throw ResponseStatusException(HttpStatus.NOT_FOUND)
```

### Null / Optional unwrapping
```kotlin
userRepository.findByEmail(email).orElseThrow { UserNotFoundException(email) }
redisRepo.findById(id) ?: throw SessionNotFoundException()
```

### Filter-level error handling
Catch all exceptions, log a warning, and return the HTTP status code directly on the response — do not propagate.

## Architecture Rules

- **Never place business logic in controllers.** Controllers delegate to services and use mappers for DTO↔domain conversion.
- Database migrations use Liquibase — add changesets to `domain/src/main/resources/db/changelog/` following `db.changelog-master.yaml`.

## Build & Verification

```bash
# Compile domain module
./gradlew :domain:build -x test --no-daemon

# Compile api module
./gradlew :api:build -x test --no-daemon

# Run all backend tests
./gradlew :domain:test
./gradlew :api:test

# Run a specific test class
./gradlew :api:test --tests "io.flavien.demo.api.session.SessionControllerTest"
```

Always verify compilation succeeds before finishing.
