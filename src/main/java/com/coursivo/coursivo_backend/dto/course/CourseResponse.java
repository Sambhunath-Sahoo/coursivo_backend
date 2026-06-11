package com.coursivo.coursivo_backend.dto.course;

import com.coursivo.coursivo_backend.dto.section.SectionResponse;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import com.coursivo.coursivo_backend.model.DifficultyLevel;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CourseResponse(Long id, String title, String description, BigDecimal price, String currency,
		Boolean isFree, String thumbnailUrl, Instructor instructor, CourseStatus status,
		DifficultyLevel difficultyLevel, List<String> tags, LocalDateTime createdAt, LocalDateTime updatedAt,
		List<SectionResponse> sections) {

	public record Instructor(Long id, String fullName) {
	}

	public static CourseResponse from(Course course) {
		List<SectionResponse> sectionResponses = course.getSections() != null ? course.getSections()
			.stream()
			.sorted((s1, s2) -> s1.getOrder().compareTo(s2.getOrder()))
			.map(SectionResponse::from)
			.toList() : List.of();

		List<String> tags = course.getTags() != null ? List.copyOf(course.getTags()) : List.of();

		return new CourseResponse(course.getId(), course.getTitle(), course.getDescription(), course.getPrice(),
				course.getCurrency(), course.getIsFree(), course.getThumbnailUrl(),
				course.getInstructor() != null
						? new Instructor(course.getInstructor().getId(), course.getInstructor().getFullName()) : null,
				course.getStatus(), course.getDifficultyLevel(), tags, course.getCreatedAt(), course.getUpdatedAt(),
				sectionResponses);
	}
}
