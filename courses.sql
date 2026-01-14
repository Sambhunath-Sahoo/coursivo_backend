-- Coursivo (PostgreSQL) — courses table
-- Source of truth: the JPA model `com.coursivo.coursivo_backend.model.Course`

CREATE TABLE IF NOT EXISTS courses (
  id BIGSERIAL PRIMARY KEY,

  title VARCHAR(255) NOT NULL,
  description TEXT,

  price NUMERIC(10, 2) NOT NULL DEFAULT 0.00,
  currency VARCHAR(3) NOT NULL DEFAULT 'INR',
  is_free BOOLEAN NOT NULL DEFAULT FALSE,

  thumbnail_url VARCHAR(2048),

  instructor_id BIGINT NOT NULL,
  status VARCHAR(16) NOT NULL DEFAULT 'DRAFT',

  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT fk_courses_instructor
    FOREIGN KEY (instructor_id) REFERENCES users(id),

  -- If a course is marked free, its price must be 0.
  CONSTRAINT chk_courses_free_price
    CHECK (is_free = FALSE OR price = 0.00)
);

CREATE INDEX IF NOT EXISTS idx_courses_instructor_id ON courses (instructor_id);
CREATE INDEX IF NOT EXISTS idx_courses_status ON courses (status);

