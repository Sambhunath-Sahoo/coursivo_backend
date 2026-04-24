package com.coursivo.coursivo_backend.kafka;

import com.coursivo.coursivo_backend.event.EnrollmentEvent;
import com.coursivo.coursivo_backend.service.SendGridEmailService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

	private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);

	private final SendGridEmailService emailService;

	public NotificationConsumer(SendGridEmailService emailService) {
		this.emailService = emailService;
	}

	@KafkaListener(topics = "${kafka.topics.enrollment}", groupId = "notification-group",
			containerFactory = "enrollmentKafkaListenerContainerFactory")
	public void handleEnrollmentEvent(ConsumerRecord<String, EnrollmentEvent> record) {
		EnrollmentEvent event = record.value();
		log.info("Received EnrollmentEvent: enrollmentId={}, student={}, course={}", event.getEnrollmentId(),
				event.getStudentEmail(), event.getCourseTitle());

		processNotifications(event);
	}

	private void processNotifications(EnrollmentEvent event) {
		try {
			emailService.sendEnrollmentConfirmationToStudent(event.getStudentEmail(), event.getStudentName(),
					event.getCourseTitle());
			emailService.sendNewEnrollmentAlertToInstructor(event.getInstructorEmail(), event.getInstructorName(),
					event.getStudentName(), event.getCourseTitle());
			log.info("Email notifications sent for enrollmentId={}", event.getEnrollmentId());
		} catch (Exception e) {
			log.error("Failed to send email notifications for enrollmentId={}: {}", event.getEnrollmentId(),
					e.getMessage(), e);
		}
	}

}
