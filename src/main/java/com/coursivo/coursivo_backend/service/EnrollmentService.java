package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.dto.enrollment.EnrollmentResponse;
import com.coursivo.coursivo_backend.event.EnrollmentEvent;
import com.coursivo.coursivo_backend.exception.ResourceNotFoundException;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.Enrollment;
import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.repository.CourseRepository;
import com.coursivo.coursivo_backend.repository.EnrollmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EnrollmentService {

	private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

	private final EnrollmentRepository enrollmentRepository;

	private final CourseRepository courseRepository;

	private final ApplicationEventPublisher eventPublisher;

	public EnrollmentService(EnrollmentRepository enrollmentRepository, CourseRepository courseRepository,
			ApplicationEventPublisher eventPublisher) {
		this.enrollmentRepository = enrollmentRepository;
		this.courseRepository = courseRepository;
		this.eventPublisher = eventPublisher;
	}

	@Transactional
	public EnrollmentResponse enrollInCourse(Long courseId, User user) {
		Course course = courseRepository.findById(courseId)
			.orElseThrow(() -> new ResourceNotFoundException("Course not found"));

		if (enrollmentRepository.existsByUserAndCourse(user, course)) {
			throw new IllegalStateException("User is already enrolled in this course");
		}

		Enrollment enrollment = new Enrollment();
		enrollment.setUser(user);
		enrollment.setCourse(course);

		Enrollment saved = enrollmentRepository.save(enrollment);

		EnrollmentEvent event = new EnrollmentEvent(saved.getId(), user.getId(), user.getEmail(), user.getFullName(),
				course.getId(), course.getTitle(), course.getInstructor().getEmail(),
				course.getInstructor().getFullName(), saved.getEnrolledAt());
		eventPublisher.publishEvent(event);

		return EnrollmentResponse.from(saved);
	}

	public List<EnrollmentResponse> getMyEnrollments(User user) {
		return enrollmentRepository.findByUserOrderByEnrolledAtDesc(user)
			.stream()
			.map(EnrollmentResponse::from)
			.toList();
	}

	public boolean checkEnrollment(Long courseId, User user) {
		Course course = courseRepository.findById(courseId)
			.orElseThrow(() -> new ResourceNotFoundException("Course not found"));
		return enrollmentRepository.existsByUserAndCourse(user, course);
	}

}
