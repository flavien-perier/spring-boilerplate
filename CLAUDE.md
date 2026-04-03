# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build all modules
./gradlew build

# Run the API in dev mode (uses Docker Compose for dependencies)
./gradlew :api:bootRun

# Run all tests
./gradlew test

# Run tests for a specific module
./gradlew :api:test
./gradlew :domain:test

# Build GraalVM native image (requires GraalVM JDK)
./gradlew :api:nativeCompile -Pnative --no-daemon

# Clean build artifacts
./gradlew clean
```

## Architecture Overview

This is a **multi-module Gradle project** with OpenAPI-driven development:

```
root (demo-parent)
├── api/            - Spring Boot REST API layer (controllers, mappers, security filters)
├── domain/         - Business logic, JPA entities, repositories, services
├── utils/          - Kotlin Multiplatform utilities (JVM + JS targets)
├── openapi/        - OpenAPI 3.1.1 spec (index.yaml) → generates Spring controller interfaces
├── frontend/       - Vue3 + TypeScript app (receives generated API client from openapi module)
└── component-library/ - Reusable Vue3 components
```

### Key Design Pattern: OpenAPI-First

The OpenAPI spec at `openapi/src/main/openapi/index.yaml` is the single source of truth:
- `kotlin-spring` generator → Spring Controller interfaces consumed by `api/`
- `typescript-axios` generator → TypeScript API client consumed by `frontend/`
- Code generation runs automatically during Gradle build

### Layer Responsibilities

- **`api/`**: Controllers implement generated interfaces, use MapStruct mappers for DTO↔entity conversion, session authentication filter
- **`domain/`**: JPA entities, Spring Data repositories, service layer, session/token management, password handling
- **`utils/`**: Shared Kotlin utilities available to both JVM backend and JS frontend

## Local Development

Start dependencies with Docker Compose (auto-started by `dev` profile):

```bash
docker compose up -d  # PostgreSQL 16, Valkey 7 (Redis), SMTP4Dev
```

The `dev` Spring profile (`application-dev.properties`) enables Docker Compose auto-start.

### Required Environment Variables

Configured in `domain/src/main/resources/domain.properties`:
- Database: `POSTGRES_URL`, `POSTGRES_USER`, `POSTGRES_PASSWORD`
- Cache: `VALKEY_HOST`, `VALKEY_PORT`, `VALKEY_PASSWORD`
- Mail: `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD`, `SMTP_AUTH`, `SMTP_STARTTLS`, `MAIL_ACCOUNT_CREATOR`, `MAIL_DOMAIN_LINKS`

## Testing

- **Unit tests**: MockK for mocking, AssertJ for assertions
- **Integration tests**: Testcontainers (PostgreSQL), SubEthaSMTP test mail server at `127.0.0.1:8025`
- Test factories for DTOs/entities are in `api/src/test/kotlin/.../factories/`
- Mail testing utility: `MailServerUtil` in `domain/src/test/`

## Database Migrations

Liquibase manages schema migrations. Migration files are at `domain/src/main/resources/db/changelog/`.

## Deployment

- **Dockerfile**: Multi-stage build using GraalVM → minimal Alpine container with native executable
- **Helm charts**: `helm/` directory for Kubernetes (K3s) deployment including app, PostgreSQL, and Valkey
