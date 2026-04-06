package com.coursivo.coursivo_backend.dto.auth;

import com.coursivo.coursivo_backend.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO = Data Transfer Object
 *
 * WHY this exists: - Controllers should NOT accept JPA entities directly. - A DTO defines
 * exactly what the API expects from the client.
 *
 * This request is used for: POST /api/auth/register
 */
public record RegisterRequest(@Email @NotBlank String email, @NotBlank String password, @NotBlank String fullName,
		@NotNull UserRole role) {
}
