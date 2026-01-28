# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot boilerplate project that provides authentication and session/user management. The project uses an **OpenAPI-first approach** where both backend controllers and frontend HTTP clients are generated at compile time from `src/main/resources/openapi.yaml`. Access rights to resources are managed in the OpenAPI file through security schemas.

The project is compiled with **GraalVM** for optimized native image builds, significantly reducing container size. GraalVM profile optimization files are generated during tests - more comprehensive tests lead to faster native image startup.

## Technology Stack

**Backend:**
- Kotlin + Spring Boot 3.5.0
- Java 21
- Maven
- PostgreSQL (database)
- Valkey/Redis (session storage)
- Liquibase (database migrations)
- MapStruct (entity-DTO mapping)
- Spring Security (authentication/authorization)
- Testcontainers (integration tests)
- MockK (Kotlin mocking framework)

**Frontend:**
- Vue 3 + TypeScript
- Vite (build tool)
- Pinia (state management)
- Vue Router
- Vue I18n (internationalization)
- Bootstrap 5
- Font Awesome
- Axios (HTTP client - generated from OpenAPI)

**Infrastructure:**
- Docker & Docker Compose
- Helm charts (K3s deployment)
- GraalVM native image compilation

## Build Commands

### Standard Build (JVM)
```bash
mvn clean package
```

### GraalVM Native Build (Full)
```bash
mvn -Pnative clean compile spring-boot:process-aot spring-boot:process-test-aot package native:compile
```

### Skip Tests
```bash
mvn clean package -DskipTests=true
```

### Skip Frontend Build
```bash
mvn clean package -DskipNpm=true
```

### Run Tests Only
```bash
mvn test
```

### Run Single Test Class
```bash
mvn test -Dtest=YourTestClassName
```

### Frontend Development Server
```bash
cd src/main/typescript
npm run dev
```

### Frontend Build Only
```bash
cd src/main/typescript
npm run build
```

### Type Check Frontend
```bash
cd src/main/typescript
npm run type-check
```

## Development Environment

### Start Infrastructure (Docker Compose)
```bash
docker-compose up -d
```

This starts:
- PostgreSQL on `127.0.0.1:5432` (user: admin, DB: admin)
- Valkey (Redis) on `127.0.0.1:6379`
- SMTP4Dev on `127.0.0.1:25` (web UI: `127.0.0.1:8025`)

### Run Application Locally
```bash
mvn spring-boot:run
```

Application runs on `http://127.0.0.1:8080`

## Architecture

### Code Generation Flow

1. **OpenAPI Specification** (`src/main/resources/openapi.yaml`) defines all API endpoints, request/response schemas, and security requirements
2. **Backend Generation**: OpenAPI Generator Maven Plugin creates Spring controller interfaces and DTOs in packages:
   - `io.flavien.demo.api` - Controller interfaces
   - `io.flavien.demo.dto` - Data Transfer Objects
3. **Frontend Generation**: Generates TypeScript Axios client in `target/node_modules/api-generated`
4. **Implementation**: Backend controllers implement generated interfaces in their respective domain packages

### Backend Package Structure

Base package: `io.flavien.demo`

```
io.flavien.demo/
├── Application.kt                 # Main Spring Boot application entry point
├── application/                   # Application-wide configuration and shared components
│   └── mapper/                    # MapStruct mappers for application DTOs
├── config/                        # Spring configuration classes
├── core/                          # Core utilities and shared functionality
│   └── util/                      # Utility classes
├── session/                       # Session management domain
│   ├── entity/                    # JPA entities for sessions
│   ├── exception/                 # Session-specific exceptions
│   ├── filter/                    # Spring Security filters for session handling
│   ├── mapper/                    # MapStruct mappers for session DTOs
│   ├── model/                     # Domain models
│   ├── repository/                # Spring Data JPA repositories
│   ├── service/                   # Business logic services
│   └── util/                      # Session utilities
└── user/                          # User management domain
    ├── entity/                    # JPA entities for users
    ├── exception/                 # User-specific exceptions
    ├── mapper/                    # MapStruct mappers for user DTOs
    ├── model/                     # Domain models (UserRole, UserUpdate, etc.)
    ├── repository/                # Spring Data JPA repositories
    └── service/                   # Business logic (UserService, UserActivationService, ForgotPasswordService)
```

### Frontend Structure

Base directory: `src/main/typescript/src`

```
src/
├── account/                       # User account management pages
├── admin/                         # Admin pages
├── assets/                        # Static assets (images, etc.)
├── change-password/               # Change password feature
├── component-library/             # Reusable Vue components
├── core/                          # Core utilities and shared functionality
├── create-account/                # Account registration feature
├── forgot-password/               # Password reset feature
├── home/                          # Home page
├── locales/                       # i18n translation files (en, fr)
├── login/                         # Login page
├── main.ts                        # Vue app entry point
├── router.ts                      # Vue Router configuration
└── i18n.ts                        # i18n configuration
```

### Key Architectural Patterns

**Domain-Driven Structure**: Backend is organized by domains (`session`, `user`) with layers:
- `entity/` - JPA persistence layer
- `repository/` - Data access layer (Spring Data)
- `service/` - Business logic layer
- `mapper/` - DTO conversion layer (MapStruct)
- `model/` - Domain models and enums

**OpenAPI-First Design**: All API endpoints must be defined in `openapi.yaml` before implementation. Controllers implement generated interfaces, ensuring contract compliance.

**Security Model**: Access control is defined in OpenAPI security schemas (e.g., `bearer: [user, admin]`). Spring Security filters in `session/filter/` enforce authentication using sessions stored in Valkey.

**Generated API Client**: Frontend uses generated TypeScript Axios client from `api-generated` package, ensuring type-safe HTTP calls that match backend contracts.

## Database Management

Database migrations are managed with **Liquibase** in `src/main/resources/db/changelog/`. The master changelog is `db.changelog-master.yaml`.

## Important Notes

- **OpenAPI is the source of truth**: Always update `src/main/resources/openapi.yaml` when adding/modifying endpoints
- **Backend implements interfaces**: Controllers must implement generated API interfaces from `io.flavien.demo.api`
- **MapStruct for mappings**: Use MapStruct interfaces in `mapper/` packages to convert between entities and DTOs
- **Session-based auth**: Authentication uses Valkey-stored sessions, not JWT tokens (though token-based login exists alongside web-based cookie login)
- **Testing improves GraalVM**: Write comprehensive tests to generate better GraalVM profile data for faster native image startup
- **Main class**: `io.flavien.demo.ApplicationKt` is the entry point for both JVM and native builds
