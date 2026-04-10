package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.dto.common.ApiResponse;
import com.coursivo.coursivo_backend.dto.lesson.CreateLessonRequest;
import com.coursivo.coursivo_backend.dto.lesson.LessonResponse;
import com.coursivo.coursivo_backend.dto.lesson.UpdateLessonRequest;
import com.coursivo.coursivo_backend.security.CustomUserDetails;
import com.coursivo.coursivo_backend.service.LessonService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/instructor/courses/{courseId}/lessons")
public class LessonController {

	private final LessonService lessonService;

	public LessonController(LessonService lessonService) {
		this.lessonService = lessonService;
	}

	/**
	 * POST /api/instructor/courses/{courseId}/lessons
	 * Create a new lesson for a course. Requires INSTRUCTOR role and must be the course
	 * instructor.
	 */
	@PostMapping
	@PreAuthorize("hasRole('INSTRUCTOR')")
	public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
			@PathVariable Long courseId,
			@Valid @RequestBody CreateLessonRequest request,
			@AuthenticationPrincipal CustomUserDetails principal) {

		LessonResponse lesson = lessonService.createLesson(courseId, request, principal.getUser());
		return ResponseEntity.ok(ApiResponse.ok(lesson, "Lesson created successfully"));
	}

	/**
	 * GET /api/instructor/courses/{courseId}/lessons
	 * Get all lessons for a course (ordered by lesson order). Public endpoint.
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessonsByCourse(
			@PathVariable Long courseId) {
		List<LessonResponse> lessons = lessonService.getLessonsByCourse(courseId);
		return ResponseEntity.ok(ApiResponse.ok(lessons, "Lessons fetched successfully"));
	}

	/**
	 * GET /api/instructor/courses/{courseId}/lessons/{lessonId}
	 * Get a single lesson by ID. Public endpoint.
	 */
	@GetMapping("/{lessonId}")
	public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(
			@PathVariable Long courseId,
			@PathVariable Long lessonId) {
		LessonResponse lesson = lessonService.getLessonById(lessonId);
		return ResponseEntity.ok(ApiResponse.ok(lesson, "Lesson fetched successfully"));
	}

	/**
	 * PUT /api/instructor/courses/{courseId}/lessons/{lessonId}
	 * Update a lesson. Requires INSTRUCTOR role and must be the course instructor.
	 */
	@PutMapping("/{lessonId}")
	@PreAuthorize("hasRole('INSTRUCTOR')")
	public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
			@PathVariable Long courseId,
			@PathVariable Long lessonId,
			@Valid @RequestBody UpdateLessonRequest request,
			@AuthenticationPrincipal CustomUserDetails principal) {

		LessonResponse lesson = lessonService.updateLesson(lessonId, request, principal.getUser());
		return ResponseEntity.ok(ApiResponse.ok(lesson, "Lesson updated successfully"));
	}

	/**
	 * DELETE /api/instructor/courses/{courseId}/lessons/{lessonId}
	 * Delete a lesson. Requires INSTRUCTOR role and must be the course instructor.
	 */
	@DeleteMapping("/{lessonId}")
	@PreAuthorize("hasRole('INSTRUCTOR')")
	public ResponseEntity<ApiResponse<Void>> deleteLesson(
			@PathVariable Long courseId,
			@PathVariable Long lessonId,
			@AuthenticationPrincipal CustomUserDetails principal) {

		lessonService.deleteLesson(lessonId, principal.getUser());
		return ResponseEntity.ok(ApiResponse.ok(null, "Lesson deleted successfully"));
	}

	/**
	 * POST /api/instructor/courses/{courseId}/lessons/reorder
	 * Reorder lessons for a course. Requires INSTRUCTOR role and must be the course
	 * instructor.
	 *
	 * @param courseId the course ID
	 * @param orderedLessonIds list of lesson IDs in the desired order
	 * @param principal authenticated user
	 * @return success response
	 */
	@PostMapping("/reorder")
	@PreAuthorize("hasRole('INSTRUCTOR')")
	public ResponseEntity<ApiResponse<Void>> reorderLessons(
			@PathVariable Long courseId,
			@RequestBody List<Long> orderedLessonIds,
			@AuthenticationPrincipal CustomUserDetails principal) {

		lessonService.reorderLessons(courseId, orderedLessonIds, principal.getUser());
		return ResponseEntity.ok(ApiResponse.ok(null, "Lessons reordered successfully"));
	}
}
