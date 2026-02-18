# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build (skip tests)
./mvnw clean package -DskipTests

# Run application
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=CreateTransactionServiceTest

# Run only unit tests (excludes integration tests)
./mvnw test -Dtest="!*IntegrationTest"

# Run only integration tests
./mvnw test -Dtest="*IntegrationTest"

# Run with Docker Compose (app + PostgreSQL)
docker-compose up
```

Environment variables are loaded from `.env` (see `.env.example` for required keys). Integration tests use an H2 in-memory database (no external DB needed).

## Architecture

This project follows **Hexagonal (Ports & Adapters)** architecture with three main layers:

### 1. Domain (`domain/`)
Pure business models with no framework dependencies. All domain models extend `BaseDomainEntity`. Key models: `User`, `Transaction`, `Goal`, `Budget`, `GamificationProfile`, `Achievement`.

### 2. Application (`application/`)
- **`ports/in/`** — Use case interfaces (e.g., `CreateTransactionUseCase`), command objects, and query objects that define what the application can do
- **`ports/out/`** — Output port interfaces (e.g., `SaveTransactionPort`, `LoadUserPort`) that define dependencies the application needs
- **`usecase/`** — Service implementations that implement input ports and depend on output ports (19 services total)

### 3. Infrastructure (`infrastructure/`)
- **`adapters/in/web/`** — REST controllers, DTOs (`dto/request/`, `dto/response/`), and `GlobalExceptionHandler`
- **`adapters/out/persistence/`** — JPA entities, Spring Data repositories, and mappers that bridge domain models ↔ JPA entities
- **`adapters/out/notification/`** and **`adapters/out/job/`** — Other output adapters
- **`security/`** — JWT-based authentication (`TokenService`, `SecurityConfig`)
- **`config/`** — `DotenvConfig` (loads `.env`), `OpenApiConfig` (Swagger), `JacksonConfig`

### Key dependency flow
```
Controller → UseCase interface (port/in) → Service impl → Port/out interface → JPA Adapter → DB
```

Services **never** import from `infrastructure`. Infrastructure depends on application; application depends only on domain.

## Database

- **Production**: PostgreSQL 15, managed by **Flyway** (`src/main/resources/db/migration/`, V1–V11)
- **Tests**: H2 in-memory with `ddl-auto=create-drop`; Flyway is disabled in test profile
- JPA `ddl-auto=none` in production — schema changes require new Flyway migrations

## Testing

Tests use `@ActiveProfiles("test")` and `application-test.yaml`. Integration tests extend `BaseIntegrationTest` which provides `MockMvc` utilities and pre-registered user/auth token helpers. JWT secret is hardcoded in the test profile (`application-test.yaml`).

## API Documentation

Swagger UI is available at `/swagger-ui.html`. Credentials are configured via `SWAGGER_USER`/`SWAGGER_PASSWORD` env vars (defaults to `admin`/`admin` in tests).

## CI/CD

GitHub Actions (`.github/workflows/deploy.yml`) builds the Docker image and pushes to `ghcr.io`, then deploys to a VPS via SSH. The Dockerfile is a multi-stage build (Maven + JDK 21 → JRE 21 Alpine runtime) running as a non-root user.
