# Architecture & Patterns

## Layered Architecture (strict top-down)

```
Controller → Service → Repository
```

- **Controllers** handle HTTP only: parse request, call service, return response. No business logic.
- **Services** contain all business logic. The only layer that talks to repositories.
- **Repositories** are Spring Data JPA interfaces. No custom logic unless query-based.
- Never call a repository directly from a controller.
- Never call one service from another unless clearly necessary and non-circular.

## DTOs

- All API input/output goes through DTOs — never expose JPA entities directly.
- DTOs live in `dto/` grouped by domain: `dto/auth/`, `dto/course/`, `dto/common/`.
- Shared wrappers (`ApiResponse<T>`, `ApiMetaData`) live in `dto/common/`.
- Name DTOs clearly: `CreateCourseRequest`, `CourseResponse`, `LoginRequest`, `AuthResponse`.

## Entities

- Entities live in `model/` — one file per entity or enum.
- Use Lombok: `@Data`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor` as needed.
- Use `@RequiredArgsConstructor` on services/controllers injected via constructor.
- JPA relationships must be mapped explicitly — don't rely on defaults.

## Exception Handling

- All exceptions are caught centrally in `GlobalExceptionHandler` (`@ControllerAdvice`).
- Never return raw exceptions or stack traces from controllers.
- Throw meaningful exceptions from service layer; handle them in `GlobalExceptionHandler`.

## Security

- JWT-based stateless auth — no sessions.
- `SecurityConfig` defines public vs protected routes.
- Public: `/api/auth/**`, `/api/health`, `/actuator/health`
- Protected: all other endpoints require a valid Bearer JWT.
- Roles: `STUDENT`, `INSTRUCTOR` — enforce at method level with `@PreAuthorize` or in SecurityConfig.
- `CustomUserDetails` wraps the `User` entity for Spring Security.
- `JwtAuthenticationFilter` validates the token on every request.

## Configuration

- Spring beans (CORS, PasswordEncoder, etc.) live in `config/`.
- Never hardcode secrets — use `application.properties` (gitignored) or environment variables.
- Use `${ENV_VAR:defaultValue}` syntax in properties.

## Naming Conventions

- Controllers: `{Domain}Controller` — e.g., `CourseController`, `AuthController`
- Services: `{Domain}Service` — e.g., `CourseService`, `AuthService`
- Repositories: `{Entity}Repository` — e.g., `CourseRepository`, `UserRepository`
- DTOs: `{Action}{Domain}Request` / `{Domain}Response`
- Enums: PascalCase, values in UPPER_SNAKE_CASE
