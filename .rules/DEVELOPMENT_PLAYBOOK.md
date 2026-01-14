# Coursivo — Education Platform

**Stack**: React + Spring Boot + Neon (PostgreSQL)

## Project goal

Build a minimal yet production-grade education platform supporting:

- Role-based authentication
- Educator course creation
- Student learning flow with progress tracking

Focus on end-to-end feature ownership, not feature volume.

## Feature scope (locked)

### Included

- **Auth**: Student / Educator
- **Educator**
  - Create course
  - Add lessons
  - Publish course
- **Student**
  - View courses
  - Enroll
  - Learn lessons
  - Track progress

### Excluded (intentionally)

- Payments
- AI features
- Admin panel

## Step-by-step development plan

### Step 0 — Planning (mandatory)

**Tasks**

- [x] Finalize entities
- [x] Finalize API contracts
- [x] Lock tech stack

**Entities**

- User
- Course
- Lesson
- Enrollment

**Rule**

- Do not write code before this step is done.

### Step 1 — Backend setup (Day 1)

**Tasks**

- Create Spring Boot project
- Add dependencies:
  - Web
  - Security
  - JPA
  - Validation
  - PostgreSQL
  - Lombok
- Configure Neon DB connection
- Enable JPA + Hibernate

**Deliverables**

- App runs successfully
- Database connection verified

### Step 2 — Database & entities (Day 1–2)

**Tasks**

- Create JPA entities:
  - User
  - Course
  - Lesson
  - Enrollment
- Define relationships
- Add DB constraints
- Enable auto-migration (Hibernate)

**Rules**

- Use snake_case in DB
- Use camelCase in Java

**Deliverables**

- Tables created in Neon
- Relationships verified

### Step 3 — Authentication & security (Day 2–3)

**Tasks**

- Implement:
  - Signup API
  - Login API
- Add:
  - BCrypt password hashing
  - JWT generation & validation
- Configure Spring Security:
  - Stateless sessions
  - Role-based access
  - Protect APIs

**Deliverables**

- JWT auth working
- Role-based access enforced

### Step 4 — Educator APIs (Day 4–5)

**APIs to implement**

- Create course
- Get educator courses
- Add lesson
- Publish course

**Rules**

- Only EDUCATOR can access
- Only course owner can modify
- Draft courses invisible to students

**Deliverables**

- Educator flow usable via Postman

### Step 5 — Student APIs (Day 6–7)

**APIs to implement**

- List published courses
- Course details
- Enroll in course
- Get learning content
- Mark lesson completed
- Get student dashboard

**Rules**

- Prevent duplicate enrollment
- Enrollment required before learning
- Backend calculates progress

**Deliverables**

- Student flow functional via APIs

### Step 6 — Frontend setup (Day 1 parallel)

**Tasks**

- Create React project
- Setup:
  - React Router
  - Axios
  - React Query
  - Auth context setup
  - API layer abstraction

**Deliverables**

- App skeleton ready

### Step 7 — Auth UI (Day 3–4)

**Tasks**

- Login page
- Signup page
- JWT storage
- Role-based redirects
- Protected routes

**Deliverables**

- Auth flow end-to-end

### Step 8 — Educator UI (Day 5–7)

**Pages**

- Educator dashboard
- Create course page
- Course detail page

**Features**

- Course form
- Lesson management
- Publish action

**Deliverables**

- Educator can manage courses from UI

### Step 9 — Student UI (Day 8–10)

**Pages**

- Course list
- Course detail
- Learning page
- Student dashboard

**Features**

- Enroll button
- Lesson viewer
- Progress bar

**Deliverables**

- Student learning flow complete

### Step 10 — UX, error handling & polish (Day 11–12)

**Tasks**

- Loading states
- Empty states
- Error messages
- Form validation
- Role-based UI visibility

**Deliverables**

- App feels production-ready

### Step 11 — Finalization (Day 13–14)

**Tasks**

- Add README
- Add screenshots
- Deploy:
  - Frontend → Vercel
  - Backend → Render / Railway
- Fix critical bugs

**Deliverables**

- Live demo URL
- Interview-ready project

## Development rules (summary)

- Backend is source of truth
- No logic in controllers
- DTOs only, no entity exposure
- No role trust from frontend
- Feature ownership > feature count