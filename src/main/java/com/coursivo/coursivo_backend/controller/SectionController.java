package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.model.Section;
import com.coursivo.coursivo_backend.service.SectionService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class SectionController {

    private final SectionService sectionService;

    public SectionController(SectionService sectionService) {
        this.sectionService = sectionService;
    }

    @PostMapping("/sections")
    public Section createSection(@RequestBody Section section) {
        return this.sectionService.createSection(section);
    }
}
