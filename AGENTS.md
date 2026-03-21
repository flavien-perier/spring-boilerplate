# AGENTS.md — Spring Boilerplate

Guidance for agentic coding agents operating in this repository.

---

## Project Overview

Multi-module Gradle (Kotlin DSL) project. Module dependency chain: `utils` → `domain` → `api` ← `frontend`.

- **Backend**: Kotlin 2.x, Spring Boot 4.x, Java 21, JPA (PostgreSQL), Valkey (Redis), Liquibase, MapStruct
- **Frontend**: Vue 3, TypeScript, Pinia, Vite, Bootstrap, Axios
- **Infra**: Docker Compose, Helm, K3s, GraalVM native image
- **API contract**: OpenAPI spec drives code generation for both backend controllers and frontend HTTP client

---

## Build Commands

```bash
# Build all modules (compiles + tests + frontend)
./gradlew build

# Build a single module without tests
./gradlew :api:build -x test --no-daemon
./gradlew :domain:build -x test --no-daemon

# Run the Spring Boot application (dev profile with Docker Compose)
./gradlew :api:bootRun

# Build GraalVM native image (requires a GraalVM JDK)
./gradlew :api:nativeCompile -Pnative --no-daemon
```

---

## Test Commands

```bash
# Run all tests across all modules
./gradlew test

# Run tests for a specific module
./gradlew :domain:test
./gradlew :api:test

# Run a single test class
./gradlew :api:test --tests "io.flavien.demo.api.session.SessionControllerTest"
./gradlew :domain:test --tests "io.flavien.demo.domain.session.service.SessionServiceTest"

# Run a single test method (use backtick names verbatim)
./gradlew :api:test --tests "io.flavien.demo.api.session.SessionControllerTest.Should login"

# Run all E2E tests
./gradlew :api:test --tests "io.flavien.demo.api.e2e.*"

# Skip tests during a build
./gradlew :api:build -x test
```

E2E tests (`api/src/test/kotlin/.../e2e/`) use Testcontainers (PostgreSQL + Valkey) and run as ordered sequential scenarios with `@Order(N)`.

---

## Lint / Type Check

No Detekt, Ktlint, Checkstyle, or Spotless is configured. The only automated static analysis is:

```bash
# Frontend TypeScript type check (runs automatically as part of frontend build)
./gradlew :frontend:npmBuild
# Equivalent to: npm run build  →  vue-tsc --noEmit && vite build
```

---

## Code Style — Kotlin / Backend

### Formatting
- 4-space indentation; no tabs.
- K&R brace style (opening brace on same line).
- No semicolons.
- Trailing commas in multi-line parameter/argument lists.
- One blank line between class members; two blank lines between top-level declarations.

### Immutability
- Prefer `val` over `var` everywhere. Use `var` only when mutation is required (e.g., JPA entity fields updated by the framework).
- Prefer immutable collections (`listOf`, `mapOf`) unless mutation is necessary.

### Null Safety
- Compiler flag `-Xjsr305=strict` is active; treat all JSR-305 annotations as strict.
- Avoid `!!` (non-null assertion). Use safe calls (`?.`), `?: throw XxxException()`, `orElseThrow`, or `getOrNull()`.

### Naming Conventions
| Element | Convention | Example |
|---|---|---|
| Classes / interfaces | `PascalCase` | `SessionService` |
| Functions / variables | `camelCase` | `refreshToken` |
| Constants | `SCREAMING_SNAKE_CASE` | `REFRESH_TOKEN_COOKIE_NAME` |
| Packages | `lowercase.dot.separated` | `io.flavien.demo.domain.session.service` |
| JPA table / column | `snake_case` | `app_user`, `password_salt` |
| Config property prefix | `kebab-case` | `flavien-io.application.*` |

### Class Role Suffixes
- `XxxController` — implements generated `XxxApi` interface; annotated `@Controller`
- `XxxService` — `@Service`; business logic only
- `XxxRepository` — `@Repository` extending `JpaRepository` or `CrudRepository`
- `XxxMapper` — `@Mapper(componentModel = "spring")` MapStruct interface
- `XxxFilter` — extends `OncePerRequestFilter`
- `XxxException` — extends `RuntimeException` with `@ResponseStatus`
- `XxxConfiguration` / `XxxProperties` — Spring configuration beans
- `XxxTestFactory` — `object` singleton providing `initXxx(...)` methods for test fixtures

### Imports
- Prefer explicit single-symbol imports.
- Wildcard imports are acceptable only for generated packages (`import io.flavien.demo.api.dto.*`) and `java.util.*` or `org.junit.jupiter.api.*`.

### Lambdas
- Use `it` for single-argument lambdas when the meaning is clear from context.
- Name the parameter explicitly when the lambda body is long or `it` would be ambiguous.

### Logging
Declare a SLF4J logger in `companion object` at the bottom of the class:

```kotlin
companion object {
    private var logger = LoggerFactory.getLogger(XxxService::class.java)
}
```

Use string templates (`"User $email has been created"`) rather than parameterized SLF4J calls.

---

## Error Handling

### Domain Exceptions (preferred)
Extend `RuntimeException` and annotate with `@ResponseStatus`. Spring MVC maps the status automatically — no `@ControllerAdvice` is used.

```kotlin
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
class UserNotFoundException : RuntimeException {
    constructor(email: String) : super("User ($email) not found")
    constructor(userId: Long) : super("User (id: $userId) not found")
}
```

### Framework-Level Errors
Use `ResponseStatusException` for infrastructure or routing errors:

```kotlin
throw ResponseStatusException(HttpStatus.NOT_FOUND)
```

### Null / Optional Unwrapping
Use Kotlin idioms rather than `.get()`:

```kotlin
// JPA Optional
userRepository.findByEmail(email).orElseThrow { UserNotFoundException(email) }

// Redis nullable entity
redisRepo.findById(id) ?: throw SessionNotFoundException()
```

### Filter-Level Error Handling
Catch all exceptions in `OncePerRequestFilter` implementations, log a warning, and write the HTTP status directly to the response — do not propagate the exception.

---

## Module & Package Structure

```
utils/     io.flavien.demo.utils          Kotlin Multiplatform (JVM+JS) shared utilities
domain/    io.flavien.demo.domain         Entities, repositories, services, domain exceptions
           └── {session,user}/
               ├── entity/               JPA @Entity or @RedisHash classes
               ├── exception/            @ResponseStatus RuntimeException subclasses
               ├── model/                Plain data classes / enums (domain models)
               ├── repository/           Repository interfaces
               ├── service/              @Service classes
               └── util/                 Stateless helpers / object singletons
api/       io.flavien.demo.api            Controllers, mappers, filters, security config
           ├── api/  (generated)          OpenAPI-generated controller interfaces
           ├── dto/  (generated)          OpenAPI-generated DTO classes
           └── {session,user}/
               ├── mapper/               MapStruct @Mapper interfaces
               └── filter/               OncePerRequestFilter subclasses
component-library/                       Shared Vue 3 components (fio-* prefix) + PasswordUtil
frontend/  src/main/typescript           Vue 3 SPA (Vite, Pinia, Vue Router)
```

**Never place business logic in controllers.** Controllers delegate to services and use mappers to convert between DTOs and domain models.

---

## Code Generation

The OpenAPI spec (`api/src/main/resources/openapi.yaml`) is the single source of truth for the HTTP contract:

- **Backend**: generates controller interfaces and DTOs into `api/build/generated/openapi/src/main/kotlin/`
  - `apiPackage = "io.flavien.demo.api.api"` — one interface per OpenAPI tag
  - `modelPackage = "io.flavien.demo.api.dto"`
- **Frontend**: generates a TypeScript Axios client into `frontend/src/main/typescript/generated/api/`

Do not edit generated files directly. All changes to the API contract must go through the OpenAPI spec.

### OpenAPI Conventions
- Tags map 1:1 to controller interfaces: `Session` → `SessionApi`
- Security: `bearer: [user]`, `bearer: [admin]`, `bearer: [user, admin]`; public endpoints have no `security` block
- `operationId` must exactly match the controller method name in camelCase
- DTO naming: `XxxDto`, `XxxCreationDto`, `XxxUpdateDto`, `XxxUpdateAdminDto`, `XxxPageDto`
- Reuse `$ref` schemas for common primitives: `email`, `password`, `proofOfWork`, `uuid`, `token`

---

## Database Migrations

Migrations are managed with **Liquibase**. Add new changesets to:

```
domain/src/main/resources/db/changelog/
```

Follow the existing changelog master file (`db.changelog-master.yaml`). Use `snake_case` for all table and column names.

### Liquibase Conventions
- Changeset IDs: `{version}-{action}-{object}` (e.g., `1.0.0-create-table-app_user`)
- Always use `<preConditions onFail="MARK_RAN">` to make changesets idempotent
- Primary key columns: `{table_singular}_id` with `BIGINT autoIncrement`
- Always include `creation_date` and `update_date` timestamp columns
- Enums stored as `VARCHAR(N)` strings; index names: `index_{table}_{column}`

---

## Testing Conventions

- Test method names use backtick string literals describing the scenario:
  ```kotlin
  @Test
  fun `Should return 401 when token is expired`() { ... }
  ```
- **MockK** is preferred for new tests. Use `mockk<T>()`, `every { }`, and `verify { }`.
- Domain tests currently use Mockito (`@Mock`, `@InjectMocks`) — follow existing patterns in the file being modified. Use MockK for mocking Kotlin `object` singletons in all cases (`mockkObject` / `unmockkObject`).
- **AssertJ** assertions are preferred: `assertThat(result).usingRecursiveComparison().isEqualTo(expected)`.
- Use `OffsetDateTimeTestComparator` when comparing timestamps to allow ±1 minute tolerance.
- Test fixture factories are `object` singletons named `XxxTestFactory` with `initXxx(...)` functions using default parameters:
  ```kotlin
  object UserTestFactory {
      fun initUser(email: String = "test@test.com", role: UserRole = UserRole.USER) = User(...)
  }
  ```
- Customize fixtures in individual tests with `.copy(field = value)` on data classes.
- E2E tests extend a shared base class, spin up real containers via Testcontainers, and are ordered with `@TestMethodOrder(MethodOrderer.OrderAnnotation::class)`. Shared state between ordered test methods is stored in nullable `companion object` properties.
- Use `@DynamicPropertySource` to inject container connection properties.

---

## Code Style — Frontend (Vue 3 / TypeScript)

### TypeScript
- `const` always; `var` never; `let` only when reassignment is required.
- No `any` in business logic — use proper DTO types from the generated `api-generated` package.
- All API calls go through the pre-configured Axios instances exported from `api-util.ts`.

### Vue Components
- Use `<script setup lang="ts">` (Composition API) for all components.
- Destructure reactive store state with `storeToRefs(store)`.
- Always call `onBeforeRouteLeave(store.close)` in view components to reset state on navigation.
- All user-visible text uses `$t("key")` (vue-i18n). No hardcoded strings in templates.
- Route components are lazy-loaded: `component: () => import("@/feature/feature.view.vue")`.
- Authenticated routes use `meta: { authenticated: true }`.

### Pinia Stores
- Use the **options API style**: `defineStore("name", { state, getters, actions })`.
- Always include a `computeAction: boolean` state flag to disable buttons during async calls.
- Always include an `init()` action for setup and a `close()` action calling `this.$reset()`.
- Route errors through `applicationStore.axiosException` or a local `.catch()` handler.

### Component Library
- Reusable components live in `component-library/` with a `fio-` prefix (e.g., `<fio-input-email>`).
- Components emit `update:modelValue` (v-model) and `update:isValid` for validation state.
- Use `defineOptions({ name: "ComponentName" })` for Vue DevTools naming.

---

## Infrastructure Notes

- **Dev environment**: `./gradlew :api:bootRun` starts the app with the `dev` Spring profile, which auto-starts Docker Compose services (PostgreSQL 16, Valkey 7, smtp4dev).
- **Virtual threads**: enabled via `spring.threads.virtual.enabled=true`.
- **Session tokens**: opaque random strings stored in Valkey with TTL (AccessToken: 15 min, RefreshToken: 1 year). No JWT library is used.
- **Proof-of-work**: client computes scrypt PoW before submitting passwords; the backend validates it.
- **Security roles**: `USER`, `ADMIN` — enforced at the filter layer by reading the OpenAPI spec at runtime.
