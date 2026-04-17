Write unit and integration tests for a Coursivo backend class.

Ask the user for:
1. The class to test (file path or class name)
2. Test type: unit (service), integration (controller via MockMvc), or both
3. Any specific scenarios they want covered beyond the standard set

Read the target class first, then produce tests covering:

## Service Unit Tests (`@ExtendWith(MockitoExtension.class)`)

Mock all repository and service dependencies. Cover:
- Happy path for each public method
- Not-found scenario (throw exception, verify it propagates)
- Validation edge cases (null input, empty list, duplicate)
- Verify repository interactions with `verify(...)`

```java
@ExtendWith(MockitoExtension.class)
class {Domain}ServiceTest {

    @Mock private {Domain}Repository {domain}Repository;
    @InjectMocks private {Domain}Service {domain}Service;

    @Test
    void get{Domain}_whenExists_returns{Domain}Response() {
        // given
        // when
        // then
    }

    @Test
    void get{Domain}_whenNotFound_throwsException() { ... }
}
```

## Controller Integration Tests (`@WebMvcTest`)

Mock the service with `@MockBean`. Cover:
- 200/201 happy path with correct response body (`jsonPath`)
- 400 validation failure (missing required field)
- 401 unauthenticated request
- 403 wrong role (use `@WithMockUser(roles = "...")`)
- 404 not found

```java
@WebMvcTest({Domain}Controller.class)
class {Domain}ControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean private {Domain}Service {domain}Service;

    @Test
    @WithMockUser(roles = "INSTRUCTOR")
    void create_withValidRequest_returns201() throws Exception { ... }

    @Test
    void create_withoutAuth_returns401() throws Exception { ... }
}
```

## Rules
- Test method name format: `methodName_whenCondition_thenExpectedOutcome`
- Use AssertJ: `assertThat(result).isEqualTo(...)` — not `assertEquals`
- Place tests at: `src/test/java/com/coursivo/coursivo_backend/{package}/`
- One test class per source class
- No Spring context in service tests — keep them fast
