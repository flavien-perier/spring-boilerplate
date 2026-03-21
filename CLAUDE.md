# CLAUDE.md

## Module READMEs

Each module has a `README.md` that is the authoritative reference for its structure, conventions, and build commands. Read it before working in any module.

- `api/README.md` — controllers, mappers, filters, generated code
- `batch/README.md` — Spring Batch jobs, steps, readers, processors, writers
- `domain/README.md` — entities, services, repositories, exceptions, migrations
- `utils/README.md` — Kotlin Multiplatform shared utilities (JVM + JS)
- `openapi/README.md` — OpenAPI spec, code generation targets
- `frontend/README.md` — Vue 3 views, Pinia stores, routing, i18n
- `component-library/README.md` — reusable `fio-*` Vue components
- `helm/README.md` — Kubernetes Helm chart structure and values

## Key Commands

```bash
./gradlew build          # build all modules
./gradlew :api:bootRun   # run the API (starts Docker Compose deps via dev profile)
./gradlew test           # run all tests
./gradlew clean          # clean build artifacts
```

## Agent Workflow

For any development task, invoke agents in this order:

1. **Analyst** (yellow) — maps the current state of the affected module(s) and produces an impact analysis. One analyst per module touched.
   - `api-analyst`, `batch-analyst`, `domain-analyst`, `frontend-analyst`, `component-library-analyst`, `openapi-analyst`, `helm-analyst`, `utils-analyst`, `changelog-analyst`, `gradle-analyst`

2. **Developer** (blue) — implements the change based on the analyst's report. One developer per module.
   - `domain-developer`, `api-developer`, `batch-developer`, `frontend-developer`, `component-library-developer`, `openapi-developer`, `changelog-developer`, `helm-developer`, `gradle-developer`, `utils-developer`

3. **Reviewer** (red) — reads the produced code and reports issues without modifying files.
   - `api-reviewer`, `batch-reviewer`, `domain-reviewer`, `frontend-reviewer`, `component-library-reviewer`, `openapi-reviewer`, `helm-reviewer`, `utils-reviewer`, `changelog-reviewer`, `gradle-reviewer`

4. **Tester** (green) — writes or fixes tests for the implemented code.
   - `domain-tester`, `api-tester`, `batch-tester`, `frontend-tester`, `component-library-tester`, `openapi-tester`, `helm-tester`, `utils-tester`, `changelog-tester`, `gradle-tester`

5. **Reviewer** (red) — second pass to verify the tests themselves are correct.

> Skip steps irrelevant to the task. Always run the analyst before the developer — never skip straight to implementation.
