# Coursivo — Development Rules

**Stack**: React + Spring Boot + Neon (PostgreSQL)

## Project principles (non-negotiable)

- Build feature-first, not framework-first
- Prefer clarity over cleverness
- Every feature must be:
  - Secure
  - Testable
  - Explainable in interviews
- Backend is the source of truth
- Frontend never trusts itself for:
  - Auth
  - Progress
  - Role validation

## Tech stack rules

### Frontend

- React (Vite / CRA)
- TypeScript (preferred)
- Axios for API calls
- React Router for routing
- React Query for server state
- Context only for auth/session

### Backend

- Spring Boot (REST)
- Spring Security + JWT
- Spring Data JPA
- Neon (PostgreSQL)
- Lombok allowed

### Database

- Neon (Postgres)
- Use UUID or BIGINT IDs
- DB constraints > app checks

## Database rules (Neon)

- All tables must have:
  - id
  - created_at
- Use snake_case in DB
- Use camelCase in Java & React
- Foreign keys are mandatory
- Never store derived values unless needed (except progress)

**Required tables**

- users
- courses
- lessons
- enrollments

## Backend architecture rules (Spring Boot)

### Package structure

```text
controller/
service/
repository/
entity/
dto/
security/
exception/
```

### Rules

- **Controllers**
  - Handle HTTP only
  - No business logic
- **Services**
  - All business logic lives here
- **Repositories**
  - JPA only, no logic
- **DTOs**
  - Never expose entities directly
- **Entities**
  - No API annotations

## API design rules

- RESTful URLs only
- Use nouns, not verbs
- Consistent response structure

**Example**

```json
{
  "data": {},
  "message": "Success"
}
```

### Status codes

- 200 → Success
- 201 → Created
- 400 → Bad Request
- 401 → Unauthorized
- 403 → Forbidden
- 404 → Not Found
- 409 → Conflict

## Authentication & security rules

- JWT-based authentication only
- Passwords must be BCrypt hashed
- Role-based access:
  - STUDENT
  - EDUCATOR
- Use:
  - @PreAuthorize
  - Security filters
- Never trust role from frontend

## Educator flow rules

- Educator can:
  - Create courses
  - Add lessons
  - Publish course
- Only course owner can modify it
- Draft courses are invisible to students

## Student flow rules

- Student can:
  - View published courses
  - Enroll once
  - Track progress
- Enrollment is mandatory before learning
- Progress is updated only by backend

## Progress tracking rules

- Progress stored in enrollments
- Backend calculates:
  - \(progress = completedLessons * 100 / totalLessons\)
- Frontend only displays progress
- Lesson completion is idempotent

## Frontend architecture rules (React)

### Folder structure

```text
src/
  api/
  auth/
  components/
  pages/
  routes/
  hooks/
  utils/
```

### Rules

- Pages = route-level components
- Components = reusable UI
- API logic isolated in /api
- No API calls inside components directly
- Use loading, error, empty states everywhere

## State management rules

- Auth state → Context
- Server state → React Query
- Local UI state → useState
- Never duplicate backend data in multiple places

## Error handling rules

### Backend

- Global exception handler
- Meaningful error messages
- Log errors, don’t swallow them

### Frontend

- Show user-friendly messages
- Handle:
  - Loading
  - Empty
  - Error states

## Naming conventions

### Java

- Classes → PascalCase
- Methods → camelCase

### React

- Components → PascalCase
- Hooks → useSomething

### APIs

- kebab-case URLs

### DB

- snake_case