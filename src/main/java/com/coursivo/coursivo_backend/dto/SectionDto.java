package com.coursivo.coursivo_backend.dto;

import lombok.Data;
import java.util.List;

@Data
public class SectionDto {

    private Long id;
    private String title;
    private Integer orderIndex;

    private List<LessonDto> lessons;
}
