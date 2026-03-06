# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot boilerplate project that provides authentication and session/user management. The project uses an **OpenAPI-first approach** where both backend controllers and frontend HTTP clients are generated at compile time from `api/src/main/resources/openapi.yaml`. Access rights to resources are managed in the OpenAPI file through security schemas.

The project is a **Gradle multi-module build** composed of four modules: `api`, `domain`, `frontend`, and `utils`.

GraalVM native image compilation is supported and applied conditionally via the `-Pnative` Gradle property.

## Technology Stack

**Backend:**
- Kotlin + Spring Boot 4.0.3
- Java 21
- Gradle (multi-module)
- PostgreSQL (database)
- Valkey/Redis (session storage via Spring Data Redis + Jedis)
- Liquibase (database migrations)
- MapStruct (entity-DTO mapping, via kapt)
- Spring Security (authentication/authorization)
- Testcontainers (integration tests)
- MockK (Kotlin mocking framework)

**Frontend:**
- Vue 3 + TypeScript
- Vite (build tool)
- Pinia (state management)
- Vue Router (with `createWebHistory`)
- Vue I18n (internationalization)
- Bootstrap 5
- Font Awesome
- Axios (HTTP client - generated from OpenAPI spec)

**Infrastructure:**
- Docker & Docker Compose (auto-started in `dev` profile via Spring Boot Docker Compose integration)
- Helm charts (K3s deployment, in `helm/`)
- GraalVM native image compilation (conditional, `-Pnative`)

## Gradle Module Structure

```
demo-parent/                    # Root project (build.gradle.kts)
├── api/                        # Spring Boot application (controllers, config, mappers)
├── domain/                     # Business logic (entities, repositories, services, exceptions)
├── frontend/                   # Vue 3 SPA build + TypeScript Axios client generation
└── utils/                      # Shared utilities (currently minimal)
```

- `domain` exposes its dependencies via `api()` configuration so `api` module inherits them transitively
- `api` depends on `domain` and `frontend` via `implementation(project(...))`
- The GraalVM native plugin is only applied to `:api` when `-Pnative` is passed

## Build Commands

### Run (JVM, development)
```bash
./gradlew :api:bootRun
```

### Run with dev profile (auto-starts Docker Compose)
```bash
./gradlew :api:bootRun --args='--spring.profiles.active=dev'
```

### Build (JAR)
```bash
./gradlew build
```

### Skip frontend build
```bash
./gradlew :api:bootRun -x :frontend:npmBuild -x :frontend:npmInstall -x :frontend:nodeSetup -x :frontend:openApiGenerate
```

### Run tests only
```bash
./gradlew test
```

### Run single module tests
```bash
./gradlew :api:test
./gradlew :domain:test
```

### GraalVM Native Build
```bash
./gradlew :api:nativeCompile -Pnative
```

### Frontend Development Server
```bash
cd frontend/src/main/typescript
npm run dev
```

### Frontend Build Only
```bash
cd frontend/src/main/typescript
npm run build
```

### Type Check Frontend
```bash
cd frontend/src/main/typescript
npm run type-check
```

## Development Environment

The `dev` Spring profile activates `spring.docker.compose.enabled=true`, which automatically starts Docker Compose services on boot.

To start infrastructure manually:
```bash
docker-compose up -d
```

Services:
- PostgreSQL on `127.0.0.1:5432` (user: `admin`, DB: `admin`, password: `$POSTGRES_PASSWORD`)
- Valkey (Redis) on `127.0.0.1:6379` (password: `$VALKEY_PASSWORD`)
- SMTP4Dev on `127.0.0.1:25` (web UI: `127.0.0.1:8025`)

Application runs on `http://127.0.0.1:8080`

## Architecture

### Code Generation Flow

1. **OpenAPI Specification** (`api/src/main/resources/openapi.yaml`) defines all API endpoints, schemas, and security requirements
2. **Backend Generation**: OpenAPI Generator Gradle plugin creates Spring controller interfaces and DTOs at `api/build/generated/openapi/src/main/kotlin/`:
   - `io.flavien.demo.api` — Controller interfaces
   - `io.flavien.demo.dto` — Data Transfer Objects
3. **Frontend Generation**: Generates TypeScript Axios client at `frontend/src/main/typescript/src/generated/api/`
4. **Implementation**: Backend controllers (in `api` module) implement the generated interfaces

### Module: `api`

Source root: `api/src/main/kotlin/io/flavien/demo/`

```
api/src/main/kotlin/io/flavien/demo/
├── Application.kt                          # Main Spring Boot entry point
├── application/
│   ├── ApplicationController.kt            # Implements ApplicationApi (GET /api/conf)
│   └── mapper/
│       └── ConfigurationMapper.kt
├── config/
│   ├── OpenAPIConfiguration.kt             # Parses openapi.yaml into OpenAPI bean
│   ├── SecurityConfiguration.kt            # Spring Security filter chain
│   └── WebConfiguration.kt                 # SPA fallback: forwards non-/api routes to index.html
├── session/
│   ├── SessionController.kt                # Implements SessionApi
│   ├── filter/
│   │   └── SessionAuthenticationFilter.kt  # Reads access token, populates SecurityContext
│   └── mapper/
│       ├── RefreshTokenMapper.kt
│       └── SessionMapper.kt
└── user/
    ├── UserController.kt                   # Implements UserApi
    └── mapper/
        ├── UserMapper.kt
        └── UserUpdateMapper.kt
```

Resources:
- `api/src/main/resources/application.properties` — server port, static resources, JPA, app name
- `api/src/main/resources/application-dev.properties` — imports `domain-dev.properties`, enables Docker Compose
- `api/src/main/resources/openapi.yaml` — OpenAPI specification (source of truth)

### Module: `domain`

Source root: `domain/src/main/kotlin/io/flavien/demo/`

```
domain/src/main/kotlin/io/flavien/demo/
├── config/
│   ├── ApplicationProperties.kt            # Custom app config (@ConfigurationProperties)
│   ├── MailProperties.kt
│   └── ValkeyConfiguration.kt              # Redis/Valkey connection config
├── core/
│   └── util/
│       └── RandomUtil.kt
├── session/
│   ├── entity/
│   │   ├── AccessToken.kt                  # Redis entity (session token)
│   │   └── RefreshToken.kt                 # Redis entity (refresh token)
│   ├── exception/                          # AuthenticationFailedException, BadToken*, etc.
│   ├── model/
│   │   └── Session.kt
│   ├── repository/
│   │   ├── AccessTokenRepository.kt        # Spring Data Redis repository
│   │   └── RefreshTokenRepository.kt
│   ├── service/
│   │   ├── AccessTokenService.kt
│   │   ├── PasswordService.kt
│   │   ├── RefreshTokenService.kt
│   │   └── SessionService.kt
│   └── util/
│       └── ContextUtil.kt
└── user/
    ├── entity/
    │   ├── User.kt                          # JPA entity
    │   ├── UserActivation.kt               # JPA entity
    │   └── ForgotPassword.kt               # JPA entity
    ├── exception/                           # UserNotFoundException, UserAlreadyExists*, etc.
    ├── model/
    │   ├── UserRole.kt                      # Enum: USER, ADMIN
    │   └── UserUpdate.kt
    ├── repository/
    │   ├── UserRepository.kt               # Spring Data JPA
    │   ├── UserActivationRepository.kt     # Spring Data Redis
    │   └── ForgotPasswordRepository.kt     # Spring Data Redis
    └── service/
        ├── UserService.kt
        ├── UserActivationService.kt
        └── ForgotPasswordService.kt
```

Resources (in `domain/src/main/resources/`):
- `domain.properties` / `domain-dev.properties` — datasource, Redis, mail, custom properties
- `db/changelog/` — Liquibase migrations (`db.changelog-master.yaml` + versioned XML files)
- `templates/` — Thymeleaf email templates (`user-activation.html`, `forgot-password.html`)
- `logback-spring.xml` — logging configuration
- `banner.txt` — Spring Boot startup banner

### Module: `frontend`

Source root: `frontend/src/main/typescript/src/`

```
src/
├── account/                    # Account management (info + security sub-views)
├── admin/                      # Admin pages (user list)
├── change-password/            # Change password flow
├── component-library/          # Reusable inputs (email, password, create-password)
├── core/                       # App shell (application.store, navbar, footer, modal, notifications)
├── create-account/             # Registration flow
├── forgot-password/            # Password reset flow
├── home/                       # Home page
├── login/                      # Login page
├── generated/api/              # Generated TypeScript Axios client (from openapi.yaml)
├── main.ts                     # Vue app entry point
├── router.ts                   # Vue Router (web history mode)
└── i18n.ts                     # i18n configuration
```

Each feature folder contains: `*.view.vue`, `*.store.ts` (Pinia), `*.router.ts`.

The built SPA output (`dist/`) is packaged as classpath static resources and served by Spring Boot at `classpath:/static/`.

### Module: `utils`

Minimal shared utilities module (`io.flavien.demo.utils`). No Spring dependencies.

### Key Architectural Patterns

**Domain-Driven Structure**: `domain` module is organized by domains (`session`, `user`) with layers: `entity/`, `repository/`, `service/`, `model/`, `exception/`.

**OpenAPI-First Design**: Always define endpoints in `api/src/main/resources/openapi.yaml` first. Controllers implement generated interfaces from `io.flavien.demo.api`.

**Security Model**: `SecurityAuthenticationFilter` reads the access token from the request and populates the `SecurityContext`. Access control rules are enforced in `SecurityConfiguration` and defined in the OpenAPI security schemas.

**Dual Storage**: Users and JPA entities (User, UserActivation, ForgotPassword entities accessed via JPA/PostgreSQL); sessions and tokens (AccessToken, RefreshToken, UserActivation, ForgotPassword via Spring Data Redis/Valkey).

**SPA Fallback**: `WebConfiguration` forwards all GET requests without a file extension that don't start with `/api` to `/index.html`, allowing Vue Router to handle client-side routing.

**Generated API Client**: Frontend uses the generated TypeScript Axios client from `src/generated/api/`, ensuring type-safe HTTP calls that match the backend contract.

## Database Management

Liquibase migrations are in `domain/src/main/resources/db/changelog/`. Master changelog: `db.changelog-master.yaml`.

## Important Notes

- **OpenAPI is the source of truth**: Always update `api/src/main/resources/openapi.yaml` when adding/modifying endpoints
- **Backend implements interfaces**: Controllers must implement generated API interfaces from `io.flavien.demo.api`
- **MapStruct via kapt**: Mapper processors run during Kotlin compilation in the `api` module
- **springdoc-openapi 2.8.5 is incompatible with Spring Boot 4.x**: `processAot` (native builds) will fail due to `TypeInformation` removal in Spring Data 4.x — upgrade springdoc before enabling native builds
- **GraalVM plugin is conditional**: Pass `-Pnative` to Gradle to enable native compilation; omitting it avoids `processAot` running during `bootRun`
- **Main class**: `io.flavien.demo.ApplicationKt` (in `api` module)
- **Session-based auth**: Authentication uses Valkey-stored tokens, not JWT. `AccessToken` and `RefreshToken` are Redis entities
