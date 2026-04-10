package com.coursivo.coursivo_backend.dto.lesson;

import com.coursivo.coursivo_backend.model.Lesson;

import java.time.LocalDateTime;

/**
 * DTO for lesson response data.
 */
public record LessonResponse(
		Long id,
		String title,
		String description,
		String videoUrl,
		String content,
		Integer order,
		Integer durationMinutes,
		Boolean isPreviewable,
		LocalDateTime createdAt,
		LocalDateTime updatedAt) {

	public static LessonResponse from(Lesson lesson) {
		return new LessonResponse(
				lesson.getId(),
				lesson.getTitle(),
				lesson.getDescription(),
				lesson.getVideoUrl(),
				lesson.getContent(),
				lesson.getOrder(),
				lesson.getDurationMinutes(),
				lesson.getIsPreviewable(),
				lesson.getCreatedAt(),
				lesson.getUpdatedAt());
	}
}
