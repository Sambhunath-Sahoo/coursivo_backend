package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Database access for Course.
 *
 * For Create Course flow, we only need save().
 */
public interface CourseRepository extends JpaRepository<Course, Long> {

	@Query("SELECT c FROM Course c LEFT JOIN FETCH c.lessons WHERE c.id = :id")
	Optional<Course> findByIdWithLessons(Long id);

	@Query("SELECT c FROM Course c LEFT JOIN FETCH c.lessons WHERE c.instructor.id = :instructorId ORDER BY c.createdAt DESC")
	List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);

	@Query("SELECT c FROM Course c LEFT JOIN FETCH c.lessons WHERE c.status = :status ORDER BY c.createdAt DESC")
	List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status);
}
