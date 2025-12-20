package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.dto.CourseResponseDto;
import com.coursivo.coursivo_backend.dto.LessonDto;
import com.coursivo.coursivo_backend.dto.SectionDto;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.model.Lesson;
import com.coursivo.coursivo_backend.model.Section;
import com.coursivo.coursivo_backend.repository.CourseRepository;
import com.coursivo.coursivo_backend.repository.LessonRepository;
import com.coursivo.coursivo_backend.repository.SectionRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final LessonRepository lessonRepository;

    public CourseService(CourseRepository courseRepository, SectionRepository sectionRepository, LessonRepository lessonRepository) {
        this.courseRepository = courseRepository;
        this.sectionRepository = sectionRepository;
        this.lessonRepository = lessonRepository;
    }

    public Course createCourse(Course course) {
        Course newCourse = new Course();
        newCourse.setUserId(course.getUserId());
        newCourse.setTitle(course.getTitle());
        newCourse.setDescription(course.getDescription());
        newCourse.setThumbnailUrl(course.getThumbnailUrl());
        newCourse.setPrice(course.getPrice());
        newCourse.setStatus(course.getStatus());
        return courseRepository.save(newCourse);
    }

    public CourseResponseDto getCourseById(Long courseId) {

        // 1️⃣ Fetch Course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 2️⃣ Fetch Sections
        List<Section> sections =
                sectionRepository.findByCourseIdOrderByOrderIndex(courseId);

        // 3️⃣ Collect section IDs
        List<Long> sectionIds = new ArrayList<>();
        for (Section section : sections) {
            sectionIds.add(section.getId());
        }

        // 4️⃣ Fetch all lessons in ONE query
        List<Lesson> lessons = sectionIds.isEmpty()
                ? new ArrayList<>()
                : lessonRepository.findBySectionIdInOrderBySectionId(sectionIds);

        // 5️⃣ Group lessons by sectionId
        Map<Long, List<Lesson>> lessonsBySection = new HashMap<>();

        for (Lesson lesson : lessons) {
            Long sectionId = lesson.getSectionId();

            // If sectionId key does not exist, create new list
            if (!lessonsBySection.containsKey(sectionId)) {
                lessonsBySection.put(sectionId, new ArrayList<>());
            }

            lessonsBySection.get(sectionId).add(lesson);
        }

        // 6️⃣ Convert Sections + Lessons into DTOs
        List<SectionDto> sectionDtos = new ArrayList<>();

        for (Section section : sections) {

            SectionDto sectionDto = new SectionDto();
            sectionDto.setId(section.getId());
            sectionDto.setTitle(section.getTitle());
            sectionDto.setOrderIndex(section.getOrderIndex());

            List<LessonDto> lessonDtos = new ArrayList<>();

            List<Lesson> sectionLessons =
                    lessonsBySection.getOrDefault(section.getId(), new ArrayList<>());

            for (Lesson lesson : sectionLessons) {

                LessonDto lessonDto = new LessonDto();
                lessonDto.setId(lesson.getId());
                lessonDto.setTitle(lesson.getTitle());
                lessonDto.setContentUrl(lesson.getContentUrl());
                lessonDto.setOrderIndex(lesson.getOrderIndex());
                lessonDto.setIsPreview(lesson.getIsPreview());

                lessonDtos.add(lessonDto);
            }

            sectionDto.setLessons(lessonDtos);
            sectionDtos.add(sectionDto);
        }

        // 7️⃣ Build final CourseResponseDto
        CourseResponseDto response = new CourseResponseDto();
        response.setId(course.getId());
        response.setTitle(course.getTitle());
        response.setDescription(course.getDescription());
        response.setThumbnailUrl(course.getThumbnailUrl());
        response.setSections(sectionDtos);

        // 8️⃣ Return response
        return response;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }
}
