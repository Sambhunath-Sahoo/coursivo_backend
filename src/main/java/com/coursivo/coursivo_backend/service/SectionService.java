package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.model.Section;
import com.coursivo.coursivo_backend.repository.SectionRepository;
import org.springframework.stereotype.Service;

@Service
public class SectionService {

    private final SectionRepository sectionRepository;

    public SectionService(SectionRepository sectionRepository) {
        this.sectionRepository = sectionRepository;
    }

    public Section createSection(Section section) {
        Section newSection = new Section();
        newSection.setCourseId(section.getCourseId());
        newSection.setTitle(section.getTitle());
        newSection.setOrderIndex(section.getOrderIndex());
        return sectionRepository.save(newSection);
    }

}
