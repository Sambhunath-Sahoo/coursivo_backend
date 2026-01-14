package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.dto.course.CreateCourseRequest;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.CourseStatus;
import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.model.UserRole;
import com.coursivo.coursivo_backend.repository.CourseRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

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

    public List<Course> getInstructorCourses(User instructor) {
        if (instructor == null || instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new AccessDeniedException("Only INSTRUCTOR can view instructor courses.");
        }

        return courseRepository.findByInstructorIdOrderByCreatedAtDesc(instructor.getId());
    }

    public List<Course> getPublishedCourses() {
        return courseRepository.findByStatusOrderByCreatedAtDesc(CourseStatus.PUBLISHED);
    }
}

