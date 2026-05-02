package com.coursivo.coursivo_backend.dto.section;

import com.coursivo.coursivo_backend.dto.lesson.LessonResponse;
import com.coursivo.coursivo_backend.model.Section;

import java.util.List;

public record SectionResponse(Long id, String title, Integer order, List<LessonResponse> lessons) {

	public static SectionResponse from(Section section) {
		List<LessonResponse> lessonResponses = section.getLessons() != null ? section.getLessons()
			.stream()
			.sorted((l1, l2) -> l1.getOrder().compareTo(l2.getOrder()))
			.map(LessonResponse::from)
			.toList() : List.of();

		return new SectionResponse(section.getId(), section.getTitle(), section.getOrder(), lessonResponses);
	}
}
