package com.coursivo.coursivo_backend.service;

import com.coursivo.coursivo_backend.event.EnrollmentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Service
public class NotificationService {

	private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

	private final SendGridEmailService emailService;

	public NotificationService(SendGridEmailService emailService) {
		this.emailService = emailService;
	}

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	@Async
	public void handleEnrollment(EnrollmentEvent event) {
		log.info("Sending enrollment emails for enrollmentId={}", event.getEnrollmentId());
		emailService.sendEnrollmentConfirmationToStudent(event.getStudentEmail(), event.getStudentName(),
				event.getCourseTitle());
		emailService.sendNewEnrollmentAlertToInstructor(event.getInstructorEmail(), event.getInstructorName(),
				event.getStudentName(), event.getCourseTitle());
		log.info("Emails sent for enrollmentId={}", event.getEnrollmentId());
	}

}
