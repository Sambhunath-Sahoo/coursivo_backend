package com.coursivo.coursivo_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class CourseResponseDto {

    private Long id;
    private String title;
    private String description;
    private String thumbnailUrl;

    private List<SectionDto> sections;
}
