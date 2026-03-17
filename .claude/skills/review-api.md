Review the current file or selected code for Coursivo backend standards.

Check for:
1. **Layer violations** — controller calling repository directly, service calling another service unnecessarily
2. **DTO boundary** — entities exposed directly in controller responses instead of DTOs
3. **Validation** — missing `@Valid` on `@RequestBody` parameters
4. **Response wrapping** — not using `ApiResponse<T>` wrapper
5. **HTTP status codes** — wrong status codes (e.g. returning 200 for created resources)
6. **Security** — hardcoded secrets, missing role checks, public endpoints that should be protected
7. **Error handling** — exceptions not propagated to `GlobalExceptionHandler`, silent failures
8. **Lombok** — missing `@RequiredArgsConstructor` or unnecessary field injection with `@Autowired`

Report each issue with file path, line reference, and a concrete fix.
