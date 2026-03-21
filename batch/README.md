# batch

Scheduled batch jobs module. Implements Spring Batch jobs that run as standalone CronJob workloads in Kubernetes.

## Responsibilities

- **Jobs** — Spring Batch `Job` definitions wiring steps together
- **Steps** — Chunk-oriented processing steps (reader → processor? → writer)
- **Readers** — `ItemReader` implementations that page through the database
- **Processors** — `ItemProcessor` implementations that filter or transform items
- **Writers** — `ItemWriter` implementations that call domain services

## Key facts

- Package root: `io.flavien.demo.batch`
- Depends on: `domain`
- Runs as a standalone Spring Boot application (separate entry point from the API)
- Each reader/processor/writer is a `@Component` bean — Spring manages their lifecycle
- Each step's wiring lives in a `XxxStepConfig` class colocated in the step's package
- The job assembly lives in `job/`
- No business logic here — all domain rules live in the `domain` module; batch only orchestrates calls

## Package layout

```
batch/src/main/kotlin/io/flavien/demo/batch/
├── Application.kt                                   Spring Boot entry point
├── job/
│   └── UserCleanupJobConfig.kt                     @Configuration — assembles steps into the job
└── step/
    ├── warn/
    │   ├── WarnInactiveUsersItemReader.kt           @Component — reads users inactive ≥ warn-threshold-months
    │   ├── WarnInactiveUsersItemProcessor.kt        @Component — skips already-warned users
    │   ├── WarnInactiveUsersItemWriter.kt           @Component — sends warning email via domain service
    │   └── WarnInactiveUsersStepConfig.kt           @Configuration — wires reader/processor/writer into step
    └── delete/
        ├── DeleteInactiveUsersItemReader.kt         @Component — reads users inactive ≥ delete-threshold-months
        ├── DeleteInactiveUsersItemWriter.kt         @Component — deletes users via domain service
        └── DeleteInactiveUsersStepConfig.kt         @Configuration — wires reader/writer into step
```

## Architecture rules

- Readers, processors, and writers are `@Component` — they declare themselves as Spring beans
- Step configs (`XxxStepConfig`) live in the same package as their components and own the `StepBuilder` logic
- The job config lives in `job/` and injects `Step` beans by `@param:Qualifier` — it has no direct dependency on readers/writers
- `CHUNK_SIZE` is a private constant in each step config's `companion object`
- Inactivity thresholds are configured via `app.batch.user-cleanup.warn-threshold-months` and `app.batch.user-cleanup.delete-threshold-months` (defaults: 11 and 12)

## Build

```bash
./gradlew :batch:build -x test --no-daemon
./gradlew :batch:test
```
