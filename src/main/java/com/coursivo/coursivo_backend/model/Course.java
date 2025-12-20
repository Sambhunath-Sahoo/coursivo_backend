package com.coursivo.coursivo_backend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(
        name= "courses"
)
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    private String description;

    private String thumbnailUrl;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String status;

    private Instant createdAt = Instant.now();
    private Instant updatedAt = Instant.now();
}
