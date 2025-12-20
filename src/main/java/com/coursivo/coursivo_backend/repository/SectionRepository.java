package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByCourseId(Long courseId);
    List<Section> findByCourseIdOrderByOrderIndex(Long courseId);
}
