package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.dto.common.ApiResponse;
import com.coursivo.coursivo_backend.dto.course.CourseResponse;
import com.coursivo.coursivo_backend.dto.course.CreateCourseRequest;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.security.CustomUserDetails;
import com.coursivo.coursivo_backend.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;

import com.coursivo.coursivo_backend.dto.course.CurriculumSaveRequest;
import com.coursivo.coursivo_backend.service.CurriculumService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {

	private final CourseService courseService;
	private final CurriculumService curriculumService;

	public CourseController(CourseService courseService, CurriculumService curriculumService) {
		this.courseService = courseService;
		this.curriculumService = curriculumService;
	}

	@GetMapping("/courses")
	public ResponseEntity<ApiResponse<List<CourseResponse>>> getPublishedCourses() {
		List<CourseResponse> courses = courseService.getPublishedCourses().stream().map(CourseResponse::from).toList();
		return ResponseEntity.ok(ApiResponse.ok(courses, "Courses fetched successfully"));
	}

	/**
	 * GET /api/courses/{id} Public endpoint — returns a single course by ID. Returns 404
	 * via {@link com.coursivo.coursivo_backend.exception.GlobalExceptionHandler} if the
	 * course does not exist.
	 */
	@GetMapping("/courses/{id}")
	public ResponseEntity<ApiResponse<CourseResponse>> getCourseById(@PathVariable Long id) {
		CourseResponse course = CourseResponse.from(courseService.getCourseById(id));
		return ResponseEntity.ok(ApiResponse.ok(course, "Course fetched successfully"));
	}

	@PostMapping("/instructor/courses")
	@PreAuthorize("hasRole('INSTRUCTOR')")
	public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CreateCourseRequest request,
			@AuthenticationPrincipal CustomUserDetails principal) {
		Course created = courseService.createCourse(request, principal.getUser());
		return ResponseEntity.ok(ApiResponse.ok(CourseResponse.from(created), "Course created successfully"));
	}

	@GetMapping("/instructor/courses")
	@PreAuthorize("hasRole('INSTRUCTOR')")
	public ResponseEntity<ApiResponse<List<CourseResponse>>> getInstructorCourses(
			@AuthenticationPrincipal CustomUserDetails principal) {
		List<CourseResponse> courses = courseService.getInstructorCourses(principal.getUser())
			.stream()
			.map(CourseResponse::from)
			.toList();
		return ResponseEntity.ok(ApiResponse.ok(courses, "Courses fetched successfully"));
	}

	@PutMapping("/instructor/courses/{id}/curriculum")
	@PreAuthorize("hasRole('INSTRUCTOR')")
	public ResponseEntity<ApiResponse<Void>> saveCurriculum(@PathVariable Long id,
			@RequestBody CurriculumSaveRequest request,
			@AuthenticationPrincipal CustomUserDetails principal) {
		curriculumService.saveCurriculum(id, request, principal.getUser());
		return ResponseEntity.ok(ApiResponse.ok(null, "Curriculum saved successfully"));
	}

}
