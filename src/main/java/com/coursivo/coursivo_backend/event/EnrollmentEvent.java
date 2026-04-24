package com.coursivo.coursivo_backend.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentEvent {

    private Long enrollmentId;
    private Long studentId;
    private String studentEmail;
    private String studentName;
    private Long courseId;
    private String courseTitle;
    private String instructorEmail;
    private String instructorName;
    private LocalDateTime enrolledAt;
}
