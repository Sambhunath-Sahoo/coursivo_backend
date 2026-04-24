package com.coursivo.coursivo_backend.kafka;

import com.coursivo.coursivo_backend.config.KafkaTopics;
import com.coursivo.coursivo_backend.event.EnrollmentEvent;
import com.coursivo.coursivo_backend.service.NotificationService;
import com.coursivo.coursivo_backend.service.SendGridEmailService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {

    private static final Logger log = LoggerFactory.getLogger(NotificationConsumer.class);
    private static final long RETRY_30S_MS = 30_000L;
    private static final long RETRY_5M_MS = 300_000L;

    private final SendGridEmailService emailService;
    private final NotificationService notificationService;

    public NotificationConsumer(SendGridEmailService emailService, NotificationService notificationService) {
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = KafkaTopics.MAIN, groupId = "notification-group")
    public void consume(ConsumerRecord<String, EnrollmentEvent> record) {
        processEvent(record);
    }

    @KafkaListener(topics = KafkaTopics.RETRY_30S, groupId = "notification-group-retry1")
    public void retry30s(ConsumerRecord<String, EnrollmentEvent> record) {
        waitForDelay(record, RETRY_30S_MS);
        processEvent(record);
    }

    @KafkaListener(topics = KafkaTopics.RETRY_5M, groupId = "notification-group-retry2")
    public void retry5m(ConsumerRecord<String, EnrollmentEvent> record) {
        waitForDelay(record, RETRY_5M_MS);
        processEvent(record);
    }

    @KafkaListener(topics = KafkaTopics.DLQ, groupId = "notification-group-dlq")
    public void handleDlq(ConsumerRecord<String, EnrollmentEvent> record) {
        try {
            EnrollmentEvent event = record.value();
            log.error("DLQ: enrollmentId={} topic={} partition={} offset={}",
                    event != null ? event.getEnrollmentId() : "null",
                    record.topic(), record.partition(), record.offset());
            // TODO: persist to DB or trigger alert for manual retry
        } catch (Exception e) {
            log.error("DLQ handler error at partition={} offset={}: {}", record.partition(), record.offset(), e.getMessage());
        }
    }

    private void processEvent(ConsumerRecord<String, EnrollmentEvent> record) {
        EnrollmentEvent event = record.value();

        if (event == null) {
            throw new IllegalArgumentException(
                    "Null event at partition=" + record.partition() + " offset=" + record.offset());
        }

        validateEvent(event);

        Long enrollmentId = event.getEnrollmentId();
        log.info("Processing EnrollmentEvent: enrollmentId={} topic={}", enrollmentId, record.topic());

        if (!notificationService.tryMarkAsProcessed(enrollmentId)) {
            log.info("Already processed enrollmentId={}, skipping", enrollmentId);
            return;
        }

        emailService.sendEnrollmentConfirmationToStudent(
                event.getStudentEmail(), event.getStudentName(), event.getCourseTitle());

        emailService.sendNewEnrollmentAlertToInstructor(
                event.getInstructorEmail(), event.getInstructorName(),
                event.getStudentName(), event.getCourseTitle());

        log.info("Emails sent for enrollmentId={}", enrollmentId);
    }

    private void waitForDelay(ConsumerRecord<?, ?> record, long delayMs) {
        long sleepMs = (record.timestamp() + delayMs) - System.currentTimeMillis();
        if (sleepMs > 0) {
            log.info("Waiting {}ms before retry on topic={}", sleepMs, record.topic());
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Retry delay interrupted", e);
            }
        }
    }

    private void validateEvent(EnrollmentEvent event) {
        if (event.getEnrollmentId() == null || event.getEnrollmentId() <= 0)
            throw new IllegalArgumentException("enrollmentId missing or invalid");
        if (event.getStudentEmail() == null || !event.getStudentEmail().contains("@"))
            throw new IllegalArgumentException("studentEmail missing or invalid");
        if (event.getInstructorEmail() == null || !event.getInstructorEmail().contains("@"))
            throw new IllegalArgumentException("instructorEmail missing or invalid");
        if (event.getStudentName() == null || event.getStudentName().isBlank())
            throw new IllegalArgumentException("studentName required");
        if (event.getCourseTitle() == null || event.getCourseTitle().isBlank())
            throw new IllegalArgumentException("courseTitle required");
    }
}
