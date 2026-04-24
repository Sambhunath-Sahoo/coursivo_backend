package com.coursivo.coursivo_backend.dto.course;

import lombok.Data;
import java.util.List;

@Data
public class CurriculumSaveRequest {
    private List<SectionDto> sections;

    @Data
    public static class SectionDto {
        private String id;
        private String title;
        private List<LessonDto> lessons;
    }

    @Data
    public static class LessonDto {
        private String id;
        private String title;
        private String type; // video, article, quiz
        private String duration; // e.g. "10:00" or "5 min read"
        private Boolean isPreview;
    }
}
