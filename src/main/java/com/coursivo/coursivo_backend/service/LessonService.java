package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.model.Lesson;
import com.coursivo.coursivo_backend.repository.LessonRepository;
import org.springframework.stereotype.Repository;

@Repository
public class LessonService {

    private final LessonRepository lessonRepository;

    public LessonService(LessonRepository lessonRepository) {
        this.lessonRepository = lessonRepository;
    }

    public Lesson createLesson(Lesson lesson) {
        Lesson newLesson = new Lesson();
        newLesson.setSectionId(lesson.getSectionId());
        newLesson.setTitle(lesson.getTitle());
        newLesson.setContentUrl(lesson.getContentUrl());
        newLesson.setOrderIndex(lesson.getOrderIndex());
        return lessonRepository.save(newLesson);
    }

}
