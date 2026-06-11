package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import com.coursivo.coursivo_backend.model.DifficultyLevel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

	@Query("SELECT c FROM Course c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.sections WHERE c.id = :id")
	Optional<Course> findByIdWithLessons(Long id);

	@Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.sections WHERE c.instructor.id = :instructorId ORDER BY c.createdAt DESC")
	List<Course> findByInstructorIdOrderByCreatedAtDesc(Long instructorId);

	@Query("SELECT DISTINCT c FROM Course c LEFT JOIN FETCH c.instructor LEFT JOIN FETCH c.sections WHERE c.status = :status ORDER BY c.createdAt DESC")
	List<Course> findByStatusOrderByCreatedAtDesc(CourseStatus status);

	/**
	 * Paginated search across title and description with optional difficulty filter.
	 * keyword must never be null — pass "" to skip the search filter.
	 */
	@Query(value = """
			SELECT c FROM Course c
			LEFT JOIN FETCH c.instructor
			WHERE c.status = :status
			  AND (:keyword = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
			  AND (:difficulty IS NULL OR c.difficultyLevel = :difficulty)
			  AND (:priceType = 'ALL'
			       OR (:priceType = 'FREE' AND c.isFree = true)
			       OR (:priceType = 'PAID' AND c.isFree = false))
			""", countQuery = """
			SELECT COUNT(c) FROM Course c
			WHERE c.status = :status
			  AND (:keyword = '' OR LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
			       OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')))
			  AND (:difficulty IS NULL OR c.difficultyLevel = :difficulty)
			  AND (:priceType = 'ALL'
			       OR (:priceType = 'FREE' AND c.isFree = true)
			       OR (:priceType = 'PAID' AND c.isFree = false))
			""")
	Page<Course> searchCourses(@Param("status") CourseStatus status, @Param("keyword") String keyword,
			@Param("difficulty") DifficultyLevel difficulty, @Param("priceType") String priceType, Pageable pageable);

}
