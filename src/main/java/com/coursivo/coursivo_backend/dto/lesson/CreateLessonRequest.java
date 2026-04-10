package com.coursivo.coursivo_backend.dto.lesson;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/**
 * DTO for creating a new lesson.
 *
 * Used for: POST /api/instructor/courses/{courseId}/lessons
 */
public record CreateLessonRequest(
		@NotBlank String title,
		String description,
		String videoUrl,
		String content,
		@NotNull @Positive Integer order,
		@Min(0) Integer durationMinutes,
		Boolean isPreviewable) {
}
