Add a new REST endpoint following Coursivo backend conventions.

Ask the user for:
1. Domain name (e.g. "course", "user", "enrollment")
2. Endpoint purpose (e.g. "get all courses", "update course status")
3. HTTP method and path
4. Required roles (STUDENT, INSTRUCTOR, or both)

Then scaffold in this order:
1. Request DTO in `dto/{domain}/{Action}{Domain}Request.java` (if needed)
2. Response DTO in `dto/{domain}/{Domain}Response.java` (if needed)
3. Service method in `service/{Domain}Service.java`
4. Controller method in `controller/{Domain}Controller.java` returning `ApiResponse<T>`
5. If public route, update `SecurityConfig`

Follow architecture rules in `.claude/rules/architecture.md` and API standards in `.claude/rules/api-standards.md`.
