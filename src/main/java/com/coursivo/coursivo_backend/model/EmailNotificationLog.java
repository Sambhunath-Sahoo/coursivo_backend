package com.coursivo.coursivo_backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Getter
@Entity
@Table(
		name = "email_notification_log",
		indexes = {
				@Index(name = "idx_email_log_enrollment_id", columnList = "enrollment_id", unique = true)
		}
)
public class EmailNotificationLog {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@Column(name = "enrollment_id", nullable = false)
	private Long enrollmentId;

	@Column(name = "sent_at", nullable = false, columnDefinition = "TIMESTAMPTZ")
	private OffsetDateTime sentAt;

	@PrePersist
	protected void onCreate() {
		sentAt = OffsetDateTime.now(ZoneOffset.UTC);
	}
}
