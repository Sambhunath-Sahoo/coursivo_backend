package com.coursivo.coursivo_backend.dto.lesson;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for updating an existing lesson.
 *
 * All fields are optional (partial update). Only provided fields will be updated.
 * Used for: PUT /api/instructor/courses/{courseId}/lessons/{lessonId}
 */
public record UpdateLessonRequest(
		@NotBlank String title,
		String description,
		String videoUrl,
		String content,
		@NotNull @Positive Integer order,
		@Min(0) Integer durationMinutes,
		Boolean isPreviewable) {
}
