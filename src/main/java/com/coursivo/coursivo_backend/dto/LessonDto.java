package com.coursivo.coursivo_backend.dto;

import lombok.Data;

@Data
public class LessonDto {

    private Long id;
    private String title;
    private String contentUrl;
    private Integer orderIndex;
    private Boolean isPreview;
}
