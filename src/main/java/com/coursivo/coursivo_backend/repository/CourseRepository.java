package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {

	@Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.sections WHERE c.id = :id")
	Optional<Course> findByIdWithLessons(Long id);

	@Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.sections WHERE c.instructor.id = :instructorId ORDER BY c.createdAt DESC")
	List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);

	@Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.sections WHERE c.status = :status ORDER BY c.createdAt DESC")
	List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status);

}
