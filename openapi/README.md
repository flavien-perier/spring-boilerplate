# openapi

Single source of truth for the HTTP API contract. Drives code generation for both the backend and the frontend.

## Responsibilities

- Defines all endpoints, request/response schemas, security requirements, and DTO structures
- Generates Kotlin Spring controller interfaces and DTOs consumed by `api/`
- Generates a TypeScript Axios client consumed by `frontend/`

## Key facts

- Spec file: `openapi/src/main/openapi/index.yaml`
- Backend generator: `kotlin-spring` → `api/build/generated/openapi/`
- Frontend generator: `typescript-axios` → `frontend/src/main/typescript/generated/api/`
- Never edit generated files — all API changes must go through the spec
- Generation runs automatically during the Gradle build

## Build

```bash
# Regenerates and compiles the backend
./gradlew :api:build -x test --no-daemon

# Regenerates and compiles the frontend TypeScript
./gradlew :frontend:npmBuild
```
