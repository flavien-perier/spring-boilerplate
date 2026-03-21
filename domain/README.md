# domain

Core business logic module. Contains everything that is not HTTP-specific.

## Responsibilities

- **Entities** — JPA (`@Entity`) and Redis (`@RedisHash`) data models
- **Repositories** — Spring Data JPA/CRUD repository interfaces
- **Services** — `@Service` classes holding all business rules
- **Exceptions** — `RuntimeException` subclasses annotated with `@ResponseStatus`
- **Database migrations** — Liquibase changesets in `src/main/resources/db/changelog/`

## Key facts

- Package root: `io.flavien.demo.domain`
- Depends on: `utils`
- Required by: `api`
- No HTTP/servlet concepts allowed in this module
- Schema changes always go through Liquibase; never alter the DB schema manually

## Build

```bash
./gradlew :domain:build -x test --no-daemon
./gradlew :domain:test
```
