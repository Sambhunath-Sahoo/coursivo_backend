# Coursivo Backend

REST API for [Coursivo](https://github.com/Sambhunath-Sahoo/coursivo_backend) — an EdTech platform where instructors create and publish courses, and students browse and enroll.

Built with **Java 21** and **Spring Boot 4.0**.

## Tech stack

| Area | Technology |
|------|------------|
| Runtime | Java 21, Spring Boot 4.0 |
| API | Spring Web MVC, Bean Validation |
| Security | Spring Security, JWT (JJWT) |
| Data | Spring Data JPA, PostgreSQL |
| Email | SendGrid |
| Build | Maven (wrapper included) |
| Container | Docker (multi-stage image) |
| CI/CD | GitHub Actions, GHCR, semantic-release |

Frontend: [coursivo-frontend-react](https://github.com/Sambhunath-Sahoo/coursivo-frontend-react) (React 19, TypeScript, Vite) — typically runs on port `5173`.

## Prerequisites

- **JDK 21**
- **PostgreSQL** (local install or [Neon](https://neon.tech) for cloud)
- **Maven** — optional; use `./mvnw` in the repo root

For releases only (CI): Node.js is used by semantic-release tooling (`package.json`); you do not need Node to run the API locally.

## Quick start

### 1. Configure the application

```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Edit `application.properties` and set at minimum:

| Property | Description |
|----------|-------------|
| `spring.datasource.url` | JDBC URL, e.g. `jdbc:postgresql://localhost:5432/coursivo` |
| `spring.datasource.username` | Database user |
| `spring.datasource.password` | Database password |
| `security.jwt.secret` | Strong secret, **32+ characters** (HS256) |
| `security.jwt.expiration-minutes` | Token lifetime in minutes |
| `spring.jpa.hibernate.ddl-auto` | `update` or `validate` for local dev |

Optional: SendGrid (`sendgrid.*`) and Kafka (`spring.kafka.*`) if you use those features.

Never commit `application.properties` — it is gitignored.

### 2. Run the API

```bash
chmod +x mvnw
./mvnw spring-boot:run
```

The server listens on **http://localhost:8080** by default (`server.port` / `PORT`).

### 3. Health check

```bash
curl http://localhost:8080/api/health
```

Actuator health is also available at `/actuator/health` when enabled.

## Docker

Build and run locally:

```bash
docker build -t coursivo-backend:local .
docker run --rm -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/coursivo \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  -e SECURITY_JWT_SECRET=your-local-dev-secret-at-least-32-chars \
  -e SECURITY_JWT_EXPIRATION_MINUTES=60 \
  coursivo-backend:local
```

Spring Boot maps environment variables to properties (e.g. `SPRING_DATASOURCE_URL` → `spring.datasource.url`).

Published images are pushed to **GitHub Container Registry**:

```text
ghcr.io/<owner>/coursivo-backend:<version>
ghcr.io/<owner>/coursivo-backend:latest   # main branch builds
```

Use a **version tag** (not `latest`) for production deploys.

## API overview

All JSON responses use the shared wrapper `ApiResponse<T>` (`data`, metadata, errors).

### Public (no JWT)

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/auth/register` | Register |
| `POST` | `/api/auth/login` | Login (returns JWT) |
| `GET` | `/api/courses` | List published courses |
| `GET` | `/api/courses/{id}` | Course detail |
| `GET` | `/api/health` | Health |

### Authenticated (`Authorization: Bearer <token>`)

| Area | Base path | Roles |
|------|-----------|--------|
| Instructor courses | `/api/instructor/courses` | `INSTRUCTOR` |
| Lessons | `/api/instructor/courses/{courseId}/lessons` | `INSTRUCTOR` |
| Enrollments | `/api/enrollments` | `STUDENT` (and related) |

Roles: **`STUDENT`**, **`INSTRUCTOR`**.

## Project layout

```text
src/main/java/com/coursivo/coursivo_backend/
├── config/         # CORS, beans
├── controller/     # REST endpoints
├── dto/            # Request/response DTOs (auth, course, common, …)
├── exception/      # GlobalExceptionHandler
├── model/          # JPA entities and enums
├── repository/     # Spring Data repositories
├── security/       # JWT filter, SecurityConfig
└── service/        # Business logic

src/main/resources/
├── application.properties.example
└── application.properties   # local only (gitignored)

.github/workflows/
├── ci.yml          # PR/main/tag builds, Docker push to GHCR
└── release.yml     # semantic-release after successful CI on main
```

## Development

```bash
# Format (Spring Java Format)
./mvnw spring-javaformat:apply

# Checkstyle
./mvnw checkstyle:check

# Package (tests currently skipped in CI; run locally when ready)
./mvnw clean package

# Tests
./mvnw test
```

## CI/CD

### Pull requests and `main`

On every **PR** and **push to `main`**, the **CI** workflow:

- Validates Spring Java Format and Checkstyle
- Builds the JAR
- Pushes a **`latest`** (and SHA) Docker image to GHCR on `main`

### Releases (automatic after green CI)

When a push to **`main`** completes and **CI succeeds**:

1. **Release** workflow runs **semantic-release** (conventional commits).
2. It creates a **git tag** `vX.Y.Z` and a **GitHub Release** (no commit back to `main` — works with branch protection).
3. The **tag** triggers **CI** again: stamps the Maven version from the tag, builds the JAR, pushes **semver** Docker tags (`X.Y.Z`, `X.Y`, `X`), and attaches the JAR to the release.

Use [Conventional Commits](https://www.conventionalcommits.org/) on merged PRs, for example:

```text
feat: add course publish endpoint
fix: validate JWT on expired tokens
chore: update dependencies
```

| Commit type | Typical bump |
|-------------|----------------|
| `feat:` | minor |
| `fix:`, `chore:`, `ci:`, … | patch |
| breaking change | major |

`pom.xml` stays at `0.1.0-SNAPSHOT` in git; the **release tag** is the version source of truth for artifacts.

### Repo settings

- **Actions → Workflow permissions**: Read and write (for releases and packages).
- **Branch protection on `main`**: require the **CI** / **build** check before merge (recommended).

## Conventions

- Expose **DTOs** at the API boundary, not JPA entities.
- Wrap responses in **`ApiResponse<T>`**.
- Keep secrets in `application.properties` or environment variables only.

Further standards: [.claude/rules/architecture.md](.claude/rules/architecture.md), [.claude/rules/api-standards.md](.claude/rules/api-standards.md).

## License

See repository license if applicable.
