package com.coursivo.coursivo_backend.dto.course;

import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CourseResponse(
        Long id,
        String title,
        String description,
        BigDecimal price,
        String currency,
        Boolean isFree,
        String thumbnailUrl,
        Instructor instructor,
        CourseStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public record Instructor(
            Long id,
            String fullName
    ) {}

    public static CourseResponse from(Course course) {
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
                course.getUpdatedAt()
        );
    }
}

