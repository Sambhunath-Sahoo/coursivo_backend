# API Standards

## Response Structure

All endpoints must return `ApiResponse<T>` from `dto/common/ApiResponse.java`.

```java
// Success
return ResponseEntity.ok(ApiResponse.success(data));

// Error (handled by GlobalExceptionHandler)
return ResponseEntity.status(HttpStatus.BAD_REQUEST)
    .body(ApiResponse.error("Message here"));
```

## HTTP Status Codes

| Situation              | Status        |
|------------------------|---------------|
| Successful GET         | 200 OK        |
| Successful POST/create | 201 Created   |
| No content             | 204 No Content|
| Bad input              | 400 Bad Request|
| Unauthenticated        | 401 Unauthorized|
| Forbidden (wrong role) | 403 Forbidden |
| Resource not found     | 404 Not Found |
| Server error           | 500 Internal Server Error |

## URL Conventions

- Base path: `/api/{domain}` — e.g., `/api/courses`, `/api/auth`
- Use lowercase, kebab-case for multi-word resources
- RESTful verbs via HTTP methods, not in URLs:
  - ✅ `POST /api/courses` to create
  - ❌ `POST /api/courses/create`
- Path variables for resource IDs: `/api/courses/{id}`

## Request Validation

- Use Bean Validation annotations (`@NotBlank`, `@Email`, `@Size`, etc.) on DTO fields.
- Add `@Valid` on `@RequestBody` parameters in controllers to trigger validation.
- Validation errors are caught by `GlobalExceptionHandler` and returned as 400.

## Authentication

- Protected endpoints require: `Authorization: Bearer <jwt_token>`
- JWT is issued on login/register via `AuthResponse`.
- Token contains: userId, email, role, expiration.

## Pagination (when applicable)

- Use Spring Data's `Pageable` for list endpoints.
- Accept `page` (0-indexed) and `size` query params.
- Return page metadata in `ApiMetaData`.

## CORS

- Configured in `CorsConfig` — allowed origins defined there.
- Do not add CORS annotations on individual controllers.

## Versioning

- No versioning prefix currently. If added in future, use `/api/v1/`.
