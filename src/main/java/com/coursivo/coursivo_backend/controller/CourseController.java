package com.coursivo.coursivo_backend.controller;

import com.coursivo.coursivo_backend.dto.CourseResponseDto;
import com.coursivo.coursivo_backend.model.Course;
import com.coursivo.coursivo_backend.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/courses")
    public Course createCourse(@RequestBody Course course) {
        return this.courseService.createCourse(course);
    }

    @GetMapping("/courses/{id}")
    public CourseResponseDto getCourse(@PathVariable Long id) {
        return this.courseService.getCourseById(id);
    }

    @GetMapping("/courses")
    public List<Course> getCourses() {
        return this.courseService.getAllCourses();
    }

}
