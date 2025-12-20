package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.model.Lesson;
import com.coursivo.coursivo_backend.service.LessonService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LessonController {

    private final LessonService lessonService;

    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping("/lessons")
    public Lesson createLesson(@RequestBody Lesson lesson) {
        return this.lessonService.createLesson(lesson);
    }
}
