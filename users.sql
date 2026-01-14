-- Coursivo (PostgreSQL) — users table
-- Source of truth: the JPA model `com.coursivo.coursivo_backend.model.User`

CREATE TABLE IF NOT EXISTS users (
  id BIGSERIAL PRIMARY KEY,
  email VARCHAR(255) NOT NULL,
  password TEXT NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  role VARCHAR(32) NOT NULL,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

  CONSTRAINT uk_users_email UNIQUE (email)
);

-- Fast lookup for login / user fetching
CREATE INDEX IF NOT EXISTS idx_users_email ON users (email);

