package com.coursivo.coursivo_backend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "courses",
		indexes = { @Index(name = "idx_courses_instructor_id", columnList = "instructor_id"),
				@Index(name = "idx_courses_status", columnList = "status") })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 255)
	private String title;

	@Column(columnDefinition = "TEXT")
	private String description;

	/**
	 * Use BigDecimal for money to avoid floating-point rounding issues.
	 *
	 * We default to 0 so instructors can create drafts without immediately deciding
	 * pricing.
	 */
	@Default
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal price = BigDecimal.ZERO;

	@Default
	@Column(nullable = false, length = 3)
	private String currency = "INR";

	@Default
	@Column(name = "is_free", nullable = false)
	private Boolean isFree = false;

	@Column(name = "thumbnail_url", length = 2048)
	private String thumbnailUrl;

	/**
	 * Many courses belong to one instructor (User). LAZY: load instructor only when
	 * needed (reduces unnecessary DB work).
	 */
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "instructor_id", nullable = false)
	private User instructor;

	/**
	 * One course has many lessons. Cascade: persist/merge/remove operations cascade to
	 * lessons. Orphan removal: deleting a lesson from the list removes it from DB.
	 * Ordered by lesson_order ascending.
	 */
	@OneToMany(mappedBy = "course", cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Lesson> lessons = new ArrayList<>();

	@Default
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 16)
	private CourseStatus status = CourseStatus.DRAFT;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at", nullable = false)
	private LocalDateTime updatedAt;

	@PrePersist
	protected void onCreate() {
		LocalDateTime now = LocalDateTime.now();
		this.createdAt = now;
		this.updatedAt = now;

		if (this.currency == null || this.currency.isBlank()) {
			this.currency = "INR";
		}
		if (this.isFree == null) {
			this.isFree = false;
		}
		if (this.price == null) {
			this.price = BigDecimal.ZERO;
		}
		if (this.status == null) {
			this.status = CourseStatus.DRAFT;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		this.updatedAt = LocalDateTime.now();
	}

	/**
	 * Helper method to add a lesson and set the bidirectional relationship.
	 */
	public void addLesson(Lesson lesson) {
		lessons.add(lesson);
		lesson.setCourse(this);
	}

	/**
	 * Helper method to remove a lesson and break the bidirectional relationship.
	 */
	public void removeLesson(Lesson lesson) {
		lessons.remove(lesson);
		lesson.setCourse(null);
	}

	/**
	 * Helper method to clear all lessons.
	 */
	public void clearLessons() {
		lessons.forEach(lesson -> lesson.setCourse(null));
		lessons.clear();
	}
}
