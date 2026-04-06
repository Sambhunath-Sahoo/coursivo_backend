package com.coursivo.coursivo_backend.dto.user;

import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.model.UserRole;

import java.time.LocalDateTime;

public record UserResponse(Long id, String email, String fullName, UserRole role, Boolean isActive,
		LocalDateTime createdAt) {
	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole(), user.getIsActive(),
				user.getCreatedAt());
	}
}
