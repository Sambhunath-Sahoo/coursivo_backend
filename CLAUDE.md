# Coursivo Backend

Spring Boot 4.0 REST API for Coursivo — an EdTech platform for educators to sell courses and test series.

## Tech Stack

- **Java 21** + **Spring Boot 4.0**
- **Spring Security** + **JWT** (JJWT 0.12.6) — stateless authentication
- **Spring Data JPA** + **PostgreSQL** (Neon in production)
- **Lombok** for boilerplate reduction
- **Maven** for build management
- **Bean Validation** for request validation

## Project Structure

```
src/main/java/com/coursivo/coursivo_backend/
├── config/          # CORS, PasswordEncoder, and other config beans
├── controller/      # REST controllers — one per domain
├── dto/             # Request/Response DTOs grouped by domain
│   ├── auth/        # LoginRequest, RegisterRequest, AuthResponse
│   ├── common/      # ApiResponse, ApiMetaData (shared wrappers)
│   └── course/      # CourseResponse, CreateCourseRequest
├── exception/       # GlobalExceptionHandler (@ControllerAdvice)
├── model/           # JPA entities + enums (User, Course, UserRole, CourseStatus)
├── repository/      # Spring Data JPA repositories
├── security/        # JWT filter, CustomUserDetails, SecurityConfig
└── service/         # Business logic — one service per domain
```

## Running Locally

1. Copy `src/main/resources/application.properties.example` → `src/main/resources/application.properties`
2. Fill in: DB URL, DB credentials, JWT secret (32+ chars), JWT expiration minutes
3. `./mvnw spring-boot:run`

Server runs on port `8080` by default.

## Key Conventions

- All API responses are wrapped in `ApiResponse<T>` from `dto/common/`
- Never expose JPA entities directly — always use DTOs at the controller boundary
- Auth endpoints (`/api/auth/**`) and health (`/actuator/health`, `/api/health`) are public
- All other endpoints require a valid JWT Bearer token
- Roles: `STUDENT`, `INSTRUCTOR`

## Related Project

Frontend: `../coursivo-frontend-react` — React 19 + TypeScript + Vite, runs on port 5173

## Rules

- [Architecture & Patterns](.claude/rules/architecture.md)
- [API Standards](.claude/rules/api-standards.md)

## Response Format

Always end each response with a **## Summary** section listing what was accomplished.
