# frontend

Vue 3 + TypeScript single-page application served by the Spring Boot backend.

## Responsibilities

- Views, Pinia stores, and Vue Router entries organised by feature
- Consumes the TypeScript Axios client generated from the OpenAPI spec
- Handles authentication (session cookies, proof-of-work), i18n, and global notifications

## Tech stack

- **Vue 3** — Composition API via `<script setup lang="ts">`
- **Pinia** — state management (options API style)
- **Vue Router** — feature-based routing
- **Bootstrap** — CSS framework
- **vue-i18n** — internationalisation
- **Axios** — HTTP (via generated API client)

## Key facts

- Source root: `frontend/src/main/typescript/src/`
- Generated API client: `frontend/src/main/typescript/generated/api/` — do not edit
- All user-facing strings use `$t("key")` — no hardcoded text in templates
- Routes requiring authentication declare `meta: { authenticated: true }`

## Build

```bash
./gradlew :frontend:npmBuild
```
