# api

Spring Boot REST API layer. Sits between the HTTP boundary and the `domain` business logic.

## Responsibilities

- **Controllers** — implement generated OpenAPI interfaces; delegate entirely to services
- **Mappers** — MapStruct interfaces for DTO ↔ domain model conversion
- **Filters** — `OncePerRequestFilter` subclasses handling session authentication
- **Configuration** — Spring security and application config beans

## Key facts

- Package root: `io.flavien.demo.api`
- Depends on: `domain`, generated OpenAPI code (controller interfaces + DTOs)
- Generated code lives in `api/build/generated/openapi/` — never edit it directly
- No business logic in controllers; no HTTP concepts in the domain layer

## Build

```bash
./gradlew :api:build -x test --no-daemon
./gradlew :api:test
```
