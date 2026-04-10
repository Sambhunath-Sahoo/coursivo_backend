package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Database access for Lesson.
 */
public interface LessonRepository extends JpaRepository<Lesson, Long> {

	List<Lesson> findByCourseIdOrderByOrderAsc(Long courseId);

	void deleteByCourseId(Long courseId);

	boolean existsByCourseIdAndOrder(Long courseId, Integer order);
}
