package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.dto.course.CreateCourseRequest;
import com.coursivo.coursivo_backend.dto.course.UpdateCourseRequest;
import com.coursivo.coursivo_backend.exception.ResourceNotFoundException;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import com.coursivo.coursivo_backend.model.DifficultyLevel;
import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.model.UserRole;
import com.coursivo.coursivo_backend.repository.CourseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CourseService {

	private final CourseRepository courseRepository;

	public CourseService(CourseRepository courseRepository) {
		this.courseRepository = courseRepository;
	}

	@Transactional
	public Course createCourse(CreateCourseRequest request, User instructor) {
		if (instructor == null || instructor.getRole() != UserRole.INSTRUCTOR) {
			throw new AccessDeniedException("Only INSTRUCTOR can create a course.");
		}

		BigDecimal requestedPrice = request.price();
		boolean isFree = (requestedPrice == null) || (requestedPrice.compareTo(BigDecimal.ZERO) == 0);
		BigDecimal finalPrice = isFree ? BigDecimal.ZERO : requestedPrice;

		Course course = Course.builder()
			.title(request.title().trim())
			.description(request.description())
			.price(finalPrice)
			.isFree(isFree)
			.thumbnailUrl(request.thumbnailUrl())
			.instructor(instructor)
			.status(CourseStatus.DRAFT)
			.build();

		return courseRepository.save(course);
	}

	@Transactional
	public List<Course> getInstructorCourses(User instructor) {
		if (instructor == null || instructor.getRole() != UserRole.INSTRUCTOR) {
			throw new AccessDeniedException("Only INSTRUCTOR can view instructor courses.");
		}

		return courseRepository.findByInstructorIdOrderByCreatedAtDesc(instructor.getId());
	}

	@Transactional
	public List<Course> getPublishedCourses() {
		return courseRepository.findByStatusOrderByCreatedAtDesc(CourseStatus.PUBLISHED);
	}

	/**
	 * Paginated + filtered search over published courses.
	 * @param keyword case-insensitive keyword matched against title and description (null
	 * = no filter)
	 * @param difficulty optional difficulty filter (null = all)
	 * @param priceType "ALL" | "FREE" | "PAID"
	 * @param sortBy "newest" | "price_asc" | "price_desc" | "name_asc" | "name_desc"
	 * @param page 0-indexed page number
	 * @param size page size (default 50, max 200)
	 */
	@Transactional(readOnly = true)
	public Page<Course> searchCourses(String keyword, DifficultyLevel difficulty, String priceType, String sortBy,
			int page, int size) {

		// Normalise inputs — never pass null keyword (PostgreSQL can't infer bytea vs
		// varchar)
		String kw = (keyword != null && !keyword.isBlank()) ? keyword.trim() : "";
		String pt = (priceType != null && !priceType.isBlank()) ? priceType.toUpperCase() : "ALL";
		if (!pt.equals("FREE") && !pt.equals("PAID")) {
			pt = "ALL";
		}

		Sort sort = switch (sortBy == null ? "newest" : sortBy.toLowerCase()) {
			case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
			case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
			case "name_asc" -> Sort.by(Sort.Direction.ASC, "title");
			case "name_desc" -> Sort.by(Sort.Direction.DESC, "title");
			default -> Sort.by(Sort.Direction.DESC, "createdAt");
		};

		Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(1, Math.min(size, 200)), sort);

		return courseRepository.searchCourses(CourseStatus.PUBLISHED, kw, difficulty, pt, pageable);
	}

	/**
	 * Returns a single course by ID regardless of status (so instructors can also preview
	 * drafts). Throws 404 if not found.
	 */
	@Transactional
	public Course getCourseById(Long id) {
		return courseRepository.findByIdWithLessons(id).orElseThrow(() -> new ResourceNotFoundException("Course", id));
	}

	@Transactional
	public Course updateCourse(Long courseId, UpdateCourseRequest request, User instructor) {
		Course course = courseRepository.findById(courseId)
			.orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

		if (!course.getInstructor().getId().equals(instructor.getId())) {
			throw new AccessDeniedException("You are not authorized to update this course.");
		}

		BigDecimal requestedPrice = request.price();
		boolean isFree = (requestedPrice == null) || (requestedPrice.compareTo(BigDecimal.ZERO) == 0);
		BigDecimal finalPrice = isFree ? BigDecimal.ZERO : requestedPrice;

		course.setTitle(request.title().trim());
		course.setDescription(request.description());
		course.setPrice(finalPrice);
		course.setIsFree(isFree);
		course.setThumbnailUrl(request.thumbnailUrl());

		return courseRepository.save(course);
	}

	@Transactional
	public Course publishCourse(Long courseId, User instructor) {
		Course course = courseRepository.findByIdWithLessons(courseId)
			.orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

		if (!course.getInstructor().getId().equals(instructor.getId())) {
			throw new AccessDeniedException("You are not authorized to publish this course.");
		}

		if (course.getSections().isEmpty()) {
			throw new IllegalArgumentException("Course must have at least one section before publishing.");
		}

		if (course.getLessons().isEmpty()) {
			throw new IllegalArgumentException("Course must have at least one lesson before publishing.");
		}

		course.setStatus(CourseStatus.PUBLISHED);
		return courseRepository.save(course);
	}

}
