package com.coursivo.coursivo_backend.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record CreateCourseRequest(
        @NotBlank String title,
        String description,
        @PositiveOrZero BigDecimal price,
        String thumbnailUrl
) {}

