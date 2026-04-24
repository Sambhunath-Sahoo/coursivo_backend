package com.coursivo.coursivo_backend.kafka;

import com.coursivo.coursivo_backend.config.KafkaTopics;
import com.coursivo.coursivo_backend.event.EnrollmentEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class EnrollmentEventProducer {

	private static final Logger log = LoggerFactory.getLogger(EnrollmentEventProducer.class);

	private final KafkaTemplate<String, EnrollmentEvent> kafkaTemplate;

	public EnrollmentEventProducer(KafkaTemplate<String, EnrollmentEvent> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	public void publish(EnrollmentEvent event) {
		String key = String.valueOf(event.getEnrollmentId());

		CompletableFuture<SendResult<String, EnrollmentEvent>> future = kafkaTemplate.send(KafkaTopics.MAIN, key, event);

		future.whenComplete((result, ex) -> {
			if (ex != null) {
				log.error("Failed to publish EnrollmentEvent for enrollmentId={}: {}", event.getEnrollmentId(),
						ex.getMessage());
			}
			else {
				log.info("Published EnrollmentEvent: enrollmentId={}, topic={}, partition={}, offset={}",
						event.getEnrollmentId(), result.getRecordMetadata().topic(),
						result.getRecordMetadata().partition(), result.getRecordMetadata().offset());
			}
		});
	}

}
