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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping("/courses")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getPublishedCourses() {
        List<CourseResponse> courses = courseService.getPublishedCourses().stream()
                .map(CourseResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(courses, "Courses fetched successfully"));
    }

    @PostMapping("/instructor/courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(
            @Valid @RequestBody CreateCourseRequest request,
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        Course created = courseService.createCourse(request, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(CourseResponse.from(created), "Course created successfully"));
    }

    @GetMapping("/instructor/courses")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> getInstructorCourses(
            @AuthenticationPrincipal CustomUserDetails principal
    ) {
        List<CourseResponse> courses = courseService.getInstructorCourses(principal.getUser()).stream()
                .map(CourseResponse::from)
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(courses, "Courses fetched successfully"));
    }
}

