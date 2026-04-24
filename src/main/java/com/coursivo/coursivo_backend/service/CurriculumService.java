package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.dto.course.CurriculumSaveRequest;
import com.coursivo.coursivo_backend.exception.ResourceNotFoundException;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.Lesson;
import com.coursivo.coursivo_backend.model.Section;
import com.coursivo.coursivo_backend.model.User;
import com.coursivo.coursivo_backend.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CurriculumService {

    private final CourseRepository courseRepository;

    public CurriculumService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void saveCurriculum(Long courseId, CurriculumSaveRequest request, User instructor) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new IllegalStateException("Not authorized to edit this course");
        }

        // Create maps of existing sections and lessons to reuse them
        Map<Long, Section> existingSections = course.getSections().stream()
            .collect(Collectors.toMap(Section::getId, s -> s));
        
        Map<Long, Lesson> existingLessons = course.getLessons().stream()
            .collect(Collectors.toMap(Lesson::getId, l -> l));

        List<Section> updatedSections = new ArrayList<>();
        List<Lesson> updatedLessons = new ArrayList<>();

        int sectionOrder = 1;
        for (CurriculumSaveRequest.SectionDto sectionDto : request.getSections()) {
            Section section = null;
            try {
                Long sectionId = Long.parseLong(sectionDto.getId());
                section = existingSections.get(sectionId);
            } catch (NumberFormatException ignored) {}

            if (section == null) {
                section = new Section();
                section.setCourse(course);
            }
            section.setTitle(sectionDto.getTitle());
            section.setOrder(sectionOrder++);
            
            // Rebuild section's lessons list
            List<Lesson> sectionLessons = new ArrayList<>();
            int lessonOrder = 1;
            for (CurriculumSaveRequest.LessonDto lessonDto : sectionDto.getLessons()) {
                Lesson lesson = null;
                try {
                    Long lessonId = Long.parseLong(lessonDto.getId());
                    lesson = existingLessons.get(lessonId);
                } catch (NumberFormatException ignored) {}

                if (lesson == null) {
                    lesson = new Lesson();
                    lesson.setCourse(course);
                }
                
                lesson.setSection(section);
                lesson.setTitle(lessonDto.getTitle());
                lesson.setOrder(lessonOrder++);
                lesson.setIsPreviewable(lessonDto.getIsPreview() != null && lessonDto.getIsPreview());
                
                // Parse duration string if present (e.g., "10 min read" or "12:45")
                // Simplified logic: just set default if null
                if (lesson.getDurationMinutes() == null) {
                    lesson.setDurationMinutes(10);
                }
                
                sectionLessons.add(lesson);
                updatedLessons.add(lesson);
            }
            
            section.getLessons().clear();
            section.getLessons().addAll(sectionLessons);
            updatedSections.add(section);
        }

        course.getSections().clear();
        course.getSections().addAll(updatedSections);
        
        course.getLessons().clear();
        course.getLessons().addAll(updatedLessons);

        courseRepository.save(course);
    }
}
