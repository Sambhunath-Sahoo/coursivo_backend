package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.dto.lesson.CreateLessonRequest;
import com.coursivo.coursivo_backend.dto.lesson.LessonResponse;
import com.coursivo.coursivo_backend.dto.lesson.UpdateLessonRequest;
import com.coursivo.coursivo_backend.exception.ResourceNotFoundException;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.Lesson;
import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.model.UserRole;
import com.coursivo.coursivo_backend.repository.CourseRepository;
import com.coursivo.coursivo_backend.repository.LessonRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LessonService {

	private final LessonRepository lessonRepository;
	private final CourseRepository courseRepository;

	public LessonService(LessonRepository lessonRepository, CourseRepository courseRepository) {
		this.lessonRepository = lessonRepository;
		this.courseRepository = courseRepository;
	}

	/**
	 * Create a new lesson for a course. Only the course instructor can create lessons.
	 */
	public LessonResponse createLesson(Long courseId, CreateLessonRequest request, User instructor) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

		if (course.getInstructor().getId() != instructor.getId()) {
			throw new AccessDeniedException("Only the course instructor can add lessons.");
		}

		// Check if order is already taken for this course
		if (lessonRepository.existsByCourseIdAndOrder(courseId, request.order())) {
			throw new IllegalArgumentException("Lesson order " + request.order() + " is already taken for this course.");
		}

		Lesson lesson = Lesson.builder()
				.title(request.title().trim())
				.description(request.description())
				.videoUrl(request.videoUrl())
				.content(request.content())
				.order(request.order())
				.durationMinutes(request.durationMinutes())
				.isPreviewable(request.isPreviewable() != null ? request.isPreviewable() : false)
				.course(course)
				.build();

		lesson = lessonRepository.save(lesson);
		return LessonResponse.from(lesson);
	}

	/**
	 * Get all lessons for a course, ordered by lesson order.
	 */
	public List<LessonResponse> getLessonsByCourse(Long courseId) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

		List<Lesson> lessons = lessonRepository.findByCourseIdOrderByOrderAsc(courseId);
		return lessons.stream()
				.map(LessonResponse::from)
				.toList();
	}

	/**
	 * Get a single lesson by ID.
	 */
	public LessonResponse getLessonById(Long lessonId) {
		Lesson lesson = lessonRepository.findById(lessonId)
				.orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));

		return LessonResponse.from(lesson);
	}

	/**
	 * Update a lesson. Only the course instructor can update lessons.
	 */
	public LessonResponse updateLesson(Long lessonId, UpdateLessonRequest request, User instructor) {
		Lesson lesson = lessonRepository.findById(lessonId)
				.orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));

		Course course = lesson.getCourse();
		if (course.getInstructor().getId() != instructor.getId()) {
			throw new AccessDeniedException("Only the course instructor can update lessons.");
		}

		// If order is being changed, check if it conflicts with existing lessons
		if (!request.order().equals(lesson.getOrder())) {
			if (lessonRepository.existsByCourseIdAndOrder(course.getId(), request.order())) {
				throw new IllegalArgumentException("Lesson order " + request.order() + " is already taken for this course.");
			}
		}

		lesson.setTitle(request.title().trim());
		lesson.setDescription(request.description());
		lesson.setVideoUrl(request.videoUrl());
		lesson.setContent(request.content());
		lesson.setOrder(request.order());
		lesson.setDurationMinutes(request.durationMinutes());
		lesson.setIsPreviewable(request.isPreviewable());

		lesson = lessonRepository.save(lesson);
		return LessonResponse.from(lesson);
	}

	/**
	 * Delete a lesson. Only the course instructor can delete lessons.
	 */
	public void deleteLesson(Long lessonId, User instructor) {
		Lesson lesson = lessonRepository.findById(lessonId)
				.orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));

		Course course = lesson.getCourse();
		if (course.getInstructor().getId() != instructor.getId()) {
			throw new AccessDeniedException("Only the course instructor can delete lessons.");
		}

		lessonRepository.delete(lesson);
	}

	/**
	 * Reorder lessons for a course. Updates the order of multiple lessons at once.
	 * Only the course instructor can reorder lessons.
	 *
	 * @param courseId the course ID
	 * @param orderedLessonIds list of lesson IDs in the desired order (1-indexed position)
	 * @param instructor the requesting user (must be the course instructor)
	 */
	public void reorderLessons(Long courseId, List<Long> orderedLessonIds, User instructor) {
		Course course = courseRepository.findById(courseId)
				.orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

		if (course.getInstructor().getId() != instructor.getId()) {
			throw new AccessDeniedException("Only the course instructor can reorder lessons.");
		}

		// Validate all lesson IDs belong to this course
		List<Lesson> lessons = lessonRepository.findAllById(orderedLessonIds);
		for (Lesson lesson : lessons) {
			if (lesson == null || !lesson.getCourse().getId().equals(courseId)) {
				throw new IllegalArgumentException("One or more lessons do not belong to this course.");
			}
		}

		// Update order based on position in the list
		for (int i = 0; i < orderedLessonIds.size(); i++) {
			Long lessonId = orderedLessonIds.get(i);
			Lesson lesson = lessonRepository.findById(lessonId)
					.orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));
			lesson.setOrder(i + 1); // 1-indexed order
			lessonRepository.save(lesson);
		}
	}
}
