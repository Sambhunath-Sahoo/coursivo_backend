package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.repository.EmailNotificationLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

	private final EmailNotificationLogRepository logRepository;

	public NotificationService(EmailNotificationLogRepository logRepository) {
		this.logRepository = logRepository;
	}

	// Atomic: DB unique constraint + ON CONFLICT DO NOTHING — no separate read needed.
	@Transactional
	public boolean tryMarkAsProcessed(Long enrollmentId) {
		return logRepository.insertIfAbsent(enrollmentId) == 1;
	}
}
