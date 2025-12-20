package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findBySectionIdInOrderBySectionId(List<Long> sectionIds);
}
