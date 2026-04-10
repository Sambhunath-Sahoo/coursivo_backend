package com.coursivo.coursivo_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lessons",
		indexes = { @Index(name = "idx_lessons_course_id", columnList = "course_id"),
				@Index(name = "idx_lessons_order", columnList = "lesson_order") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lesson {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Column(nullable = false, length = 255)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "video_url", length = 2048)
	private String videoUrl;

	@Column(columnDefinition = "TEXT")
	private String content;

	/**
	 * Order of the lesson within the course (1, 2, 3...). Used for sequencing.
	 */
	@NotNull
	@Column(name = "lesson_order", nullable = false)
	private Integer order;

	/**
	 * Duration in minutes. Can be null if not specified.
	 */
	@Column(name = "duration_minutes")
	private Integer durationMinutes;

	@Default
	@Column(name = "is_previewable", nullable = false)
	private Boolean isPreviewable = false;

	/**
	 * Many lessons belong to one course. LAZY: load course only when needed.
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;
		if (this.isPreviewable == null) {
			this.isPreviewable = false;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}
}
