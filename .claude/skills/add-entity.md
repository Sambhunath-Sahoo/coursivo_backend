Add a new JPA domain to the Coursivo backend — entity, repository, service, and controller — in one go.

Ask the user for:
1. Domain name (e.g. "enrollment", "review", "payment")
2. Fields on the entity (name + type for each)
3. Relationships to existing entities (e.g. ManyToOne → Course, ManyToOne → User)
4. Required roles per operation (STUDENT, INSTRUCTOR, or both)
5. Which CRUD operations are needed (list, get by id, create, update, delete)

Then scaffold in this order:

## 1. Entity — `model/{Domain}.java`
- Annotate with `@Entity`, `@Table(name = "{domain}s")`
- Use Lombok: `@Data @Builder @NoArgsConstructor @AllArgsConstructor`
- Primary key: `@Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;`
- Audit fields: `@CreationTimestamp`, `@UpdateTimestamp` where appropriate
- Map all relationships explicitly with `@ManyToOne(fetch = FetchType.LAZY)` etc.

## 2. Repository — `repository/{Domain}Repository.java`
- Extend `JpaRepository<{Domain}, Long>`
- Add only query methods needed for the service (no business logic)

## 3. DTOs
- `dto/{domain}/Create{Domain}Request.java` — fields with Bean Validation annotations
- `dto/{domain}/{Domain}Response.java` — fields to expose, never the entity directly
- Update DTO if needed for partial updates

## 4. Service — `service/{Domain}Service.java`
- Annotate with `@Service @RequiredArgsConstructor`
- Inject repository via constructor
- Implement each operation; throw meaningful exceptions for not-found cases
- Map entity → response DTO inside the service (never in controller)

## 5. Controller — `controller/{Domain}Controller.java`
- Annotate with `@RestController @RequestMapping("/api/{domain}s") @RequiredArgsConstructor`
- One method per operation; all return `ResponseEntity<ApiResponse<T>>`
- Use correct HTTP status: 200 GET, 201 POST, 204 DELETE
- Add `@PreAuthorize` for role restrictions

## 6. Security (if new public route)
- Update `SecurityConfig` if any endpoint should be public

Follow all rules in `.claude/rules/architecture.md` and `.claude/rules/api-standards.md`.
