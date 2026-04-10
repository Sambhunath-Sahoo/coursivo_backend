package com.coursivo.coursivo_backend.dto.course;

import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import com.coursivo.coursivo_backend.dto.lesson.LessonResponse;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CourseResponse(Long id, String title, String description, BigDecimal price, String currency,
		Boolean isFree, String thumbnailUrl, Instructor instructor, CourseStatus status, LocalDateTime createdAt,
		LocalDateTime updatedAt, List<LessonResponse> lessons) {
	public record Instructor(Long id, String fullName) {
	}

	public static CourseResponse from(Course course) {
		List<LessonResponse> lessonResponses = course.getLessons() != null
				? course.getLessons().stream()
						.sorted((l1, l2) -> l1.getOrder().compareTo(l2.getOrder()))
						.map(LessonResponse::from)
						.toList()
				: List.of();

		return new CourseResponse(
				course.getId(),
				course.getTitle(),
				course.getDescription(),
				course.getPrice(),
				course.getCurrency(),
				course.getIsFree(),
				course.getThumbnailUrl(),
				course.getInstructor() != null
						? new Instructor(course.getInstructor().getId(), course.getInstructor().getFullName())
						: null,
				course.getStatus(),
				course.getCreatedAt(),
				course.getUpdatedAt(),
				lessonResponses);
	}
}
