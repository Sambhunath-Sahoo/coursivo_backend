package com.coursivo.coursivo_backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Used for: POST /api/auth/login
 */
public record LoginRequest(@Email @NotBlank String email, @NotBlank String password) {
}
