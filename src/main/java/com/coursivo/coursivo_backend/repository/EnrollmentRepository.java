package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.Enrollment;
import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {
    List<Enrollment> findByUserOrderByEnrolledAtDesc(User user);
    Optional<Enrollment> findByUserAndCourse(User user, Course course);
    boolean existsByUserAndCourse(User user, Course course);
}
