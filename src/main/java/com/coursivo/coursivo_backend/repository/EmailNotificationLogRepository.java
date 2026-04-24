package com.coursivo.coursivo_backend.repository;

import com.coursivo.coursivo_backend.model.EmailNotificationLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailNotificationLogRepository extends JpaRepository<EmailNotificationLog, Long> {

	// Returns 1 if the row was inserted (this consumer "wins"), 0 if it already existed.
	// ON CONFLICT DO NOTHING makes the claim atomic — no separate read needed.
	@Modifying
	@Query(value = """
			INSERT INTO email_notification_log (enrollment_id, sent_at)
			VALUES (:enrollmentId, now())
			ON CONFLICT (enrollment_id) DO NOTHING
			""", nativeQuery = true)
	int insertIfAbsent(@Param("enrollmentId") Long enrollmentId);
}
