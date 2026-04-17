Debug a Spring Boot error in the Coursivo backend.

Ask the user to provide:
1. The full error message or stack trace
2. Which layer it originated from (startup, controller, service, repository, security filter)
3. What operation triggered it (HTTP request path + method, or startup event)

Then diagnose systematically:

## Startup Failures

**`BeanCreationException` / `UnsatisfiedDependencyException`**
- Missing `@Bean`, `@Service`, `@Repository`, or `@Component` annotation
- Circular dependency — look for two beans injecting each other
- Configuration property not set — check `application.properties`

**`DataSourceProperties` / DB connection errors**
- `application.properties` missing or DB credentials wrong
- PostgreSQL not running, or wrong port/host

## Runtime Errors

**`NullPointerException` in service**
- Repository returned `null` instead of `Optional` — check `findById` vs `getById`
- Lombok `@Builder` leaving fields null — check `@Builder.Default` or constructor

**`HttpMessageNotReadableException` (400)**
- Request body missing or malformed JSON
- JSON field name mismatch with DTO field — check `@JsonProperty` if needed

**`MethodArgumentNotValidException` (400)**
- `@Valid` on `@RequestBody` triggered Bean Validation — expected behavior
- If unexpected, check if `@Valid` is placed on the wrong parameter

**`AccessDeniedException` (403)**
- Role check failing — verify `@PreAuthorize("hasRole('INSTRUCTOR')")`
- Spring Security prefixes roles with `ROLE_` — use `hasRole('INSTRUCTOR')` not `ROLE_INSTRUCTOR`

**`JwtException` / `SignatureException` (401)**
- JWT secret mismatch between issuer and validator
- Token expired — check `JWT_EXPIRATION_MINUTES` config
- Malformed token — test with a freshly issued token

**`LazyInitializationException`**
- Accessing a `FetchType.LAZY` relationship outside a transaction
- Fix: annotate service method with `@Transactional`, or use `JOIN FETCH` in the query

**500 with no useful message**
- Check `GlobalExceptionHandler` — is the exception type handled?
- Add a catch-all `@ExceptionHandler(Exception.class)` if missing
- Enable `logging.level.org.springframework=DEBUG` temporarily

## Output
1. Root cause in plain language
2. Exact file and line to fix
3. The fix with corrected code snippet
4. How to verify the fix worked
