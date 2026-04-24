package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.dto.common.ApiResponse;
import com.coursivo.coursivo_backend.dto.enrollment.EnrollmentResponse;
import com.coursivo.coursivo_backend.security.CustomUserDetails;
import com.coursivo.coursivo_backend.service.EnrollmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/courses/{courseId}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        EnrollmentResponse response = enrollmentService.enrollInCourse(courseId, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(response, "Successfully enrolled in course"));
    }

    @GetMapping("/my-courses")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> getMyEnrollments(
            @AuthenticationPrincipal CustomUserDetails principal) {
        List<EnrollmentResponse> enrollments = enrollmentService.getMyEnrollments(principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(enrollments, "Enrollments fetched successfully"));
    }

    @GetMapping("/courses/{courseId}/check")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> checkEnrollment(
            @PathVariable Long courseId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        boolean isEnrolled = enrollmentService.checkEnrollment(courseId, principal.getUser());
        return ResponseEntity.ok(ApiResponse.ok(Map.of("isEnrolled", isEnrolled), "Enrollment status checked"));
    }
}
