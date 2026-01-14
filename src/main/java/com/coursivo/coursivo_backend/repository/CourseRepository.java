package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Database access for Course.
 *
 * For Create Course flow, we only need save().
 */
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);

    List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status);
}

