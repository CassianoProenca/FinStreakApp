# FinStreak Project Overview

FinStreak is a personal finance management application built with Java and Spring Boot. It uses gamification (streaks, achievements) to keep users engaged and consistent with their financial goals, such as saving, budgeting, and tracking transactions.

## Core Technology Stack

- **Backend:** Java 21, Spring Boot 3.5.9
- **Database:** PostgreSQL 15 (Production), H2 (Testing)
- **Migrations:** Flyway
- **Security:** Spring Security with JWT (auth0 java-jwt)
- **Architecture:** Hexagonal (Ports & Adapters)
- **API Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Build Tool:** Maven (using `mvnw` wrapper)
- **Containerization:** Docker & Docker Compose

## Project Architecture

The project follows the **Hexagonal Architecture** (Ports & Adapters) pattern, ensuring a clean separation between business logic and infrastructure concerns:

### 1. Domain (`src/main/java/.../domain/`)
- Contains pure business models (e.g., `User`, `Transaction`, `Goal`, `Budget`).
- No framework dependencies.
- All domain entities extend `BaseDomainEntity`.

### 2. Application (`src/main/java/.../application/`)
- **Ports In:** Interfaces defining the entry points to the application (Use Cases).
- **Ports Out:** Interfaces defining the dependencies needed by the application (Repositories, Email, etc.).
- **Use Cases:** Implementations of business logic that orchestrate domain entities and call output ports.

### 3. Infrastructure (`src/main/java/.../infrastructure/`)
- **Adapters In:** REST Controllers (Web adapter).
- **Adapters Out:** Persistence (JPA/Spring Data), Job scheduling, and Notifications.
- **Security:** JWT-based authentication and security configuration.
- **Config:** External configuration (Dotenv, Jackson, OpenAPI).

## Key Commands

### Build and Run
- **Build (skip tests):** `./mvnw clean package -DskipTests`
- **Run Locally:** `./mvnw spring-boot:run`
- **Run with Docker Compose:** `docker-compose up`

### Testing
- **Run all tests:** `./mvnw test`
- **Run unit tests only:** `./mvnw test -Dtest="!*IntegrationTest"`
- **Run integration tests only:** `./mvnw test -Dtest="*IntegrationTest"`

### Database Migrations
Migrations are handled by Flyway and located in `src/main/resources/db/migration/`.

## Development Conventions

- **Hexagonal Integrity:** Services in the `application` layer should never import classes from the `infrastructure` layer.
- **Entity Management:** Schema changes must be performed via Flyway migrations. JPA `ddl-auto` is set to `none` in production.
- **Testing Strategy:** 
  - Integration tests use `@ActiveProfiles("test")` and an H2 database.
  - Integration tests should extend `BaseIntegrationTest`.
- **API Standards:** All endpoints should be documented via Swagger annotations. Swagger UI is available at `/swagger-ui.html`.

## Environment Configuration
The application uses `.env` files for local configuration (see `.env.example`).
- `JWT_SECRET`: Secret key for JWT token generation.
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`: Database connection details.
- `SWAGGER_USER`, `SWAGGER_PASSWORD`: Credentials for Swagger UI access.
