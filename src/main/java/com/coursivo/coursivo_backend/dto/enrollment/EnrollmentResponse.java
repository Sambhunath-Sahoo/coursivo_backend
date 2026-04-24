package com.coursivo.coursivo_backend.dto.enrollment;

import com.coursivo.coursivo_backend.model.Enrollment;
import com.coursivo.coursivo_backend.dto.course.CourseResponse;
import java.time.LocalDateTime;

public record EnrollmentResponse(Long id, CourseResponse course, LocalDateTime enrolledAt) {
    public static EnrollmentResponse from(Enrollment enrollment) {
        return new EnrollmentResponse(
            enrollment.getId(),
            CourseResponse.from(enrollment.getCourse()),
            enrollment.getEnrolledAt()
        );
    }
}
