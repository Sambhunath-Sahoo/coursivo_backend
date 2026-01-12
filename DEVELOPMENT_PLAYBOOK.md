PROJECT_PLAYBOOK.md

Coursivo – Education Platform
React + Spring Boot + Neon (PostgreSQL)

1️⃣ Project Goal

Build a minimal yet production-grade education platform supporting:

Role-based authentication

Educator course creation

Student learning flow with progress tracking

Focus on end-to-end feature ownership, not feature volume.

2️⃣ Feature Scope (Locked)
Included

Auth (Student / Educator)

Educator:

Create course

Add lessons

Publish course

Student:

View courses

Enroll

Learn lessons

Track progress

Excluded (Intentionally)

Payments

AI features

Admin panel

3️⃣ Step-by-Step Development Plan
STEP 0 — Planning (Mandatory)

✔ Finalize entities
✔ Finalize API contracts
✔ Lock tech stack

Entities

User

Course

Lesson

Enrollment

Rule:
Do not write code before this step is done.

STEP 1 — Backend Setup (Day 1)
Tasks

Create Spring Boot project

Add dependencies:

Web

Security

JPA

Validation

PostgreSQL

Lombok

Configure Neon DB connection

Enable JPA + Hibernate

Deliverable

App runs successfully

Database connection verified

STEP 2 — Database & Entities (Day 1–2)
Tasks

Create JPA entities:

User

Course

Lesson

Enrollment

Define relationships

Add DB constraints

Enable auto-migration (Hibernate)

Rule

Use snake_case in DB

Use camelCase in Java

Deliverable

Tables created in Neon

Relationships verified

STEP 3 — Authentication & Security (Day 2–3)
Tasks

Implement:

Signup API

Login API

Add:

BCrypt password hashing

JWT generation & validation

Configure Spring Security:

Stateless sessions

Role-based access

Protect APIs

Deliverable

JWT auth working

Role-based access enforced

STEP 4 — Educator APIs (Day 4–5)
APIs to Implement

Create course

Get educator courses

Add lesson

Publish course

Rules

Only EDUCATOR can access

Only course owner can modify

Draft courses invisible to students

Deliverable

Educator flow usable via Postman

STEP 5 — Student APIs (Day 6–7)
APIs to Implement

List published courses

Course details

Enroll in course

Get learning content

Mark lesson completed

Get student dashboard

Rules

Prevent duplicate enrollment

Enrollment required before learning

Backend calculates progress

Deliverable

Student flow functional via APIs

STEP 6 — Frontend Setup (Day 1 Parallel)
Tasks

Create React project

Setup:

React Router

Axios

React Query

Auth context setup

API layer abstraction

Deliverable

App skeleton ready

STEP 7 — Auth UI (Day 3–4)
Tasks

Login page

Signup page

JWT storage

Role-based redirects

Protected routes

Deliverable

Auth flow end-to-end

STEP 8 — Educator UI (Day 5–7)
Pages

Educator dashboard

Create course page

Course detail page

Features

Course form

Lesson management

Publish action

Deliverable

Educator can manage courses from UI

STEP 9 — Student UI (Day 8–10)
Pages

Course list

Course detail

Learning page

Student dashboard

Features

Enroll button

Lesson viewer

Progress bar

Deliverable

Student learning flow complete

STEP 10 — UX, Error Handling & Polish (Day 11–12)
Tasks

Loading states

Empty states

Error messages

Form validation

Role-based UI visibility

Deliverable

App feels production-ready

STEP 11 — Finalization (Day 13–14)
Tasks

Add README

Add screenshots

Deploy:

Frontend → Vercel

Backend → Render / Railway

Fix critical bugs

Deliverable

Live demo URL

Interview-ready project

4️⃣ Development Rules (Summary)

Backend is source of truth

No logic in controllers

DTOs only, no entity exposure

No role trust from frontend

Feature ownership > feature count