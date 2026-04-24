package com.coursivo.coursivo_backend.config;

import com.coursivo.coursivo_backend.event.EnrollmentEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
public class KafkaErrorHandlerConfig {

    private static final Logger log = LoggerFactory.getLogger(KafkaErrorHandlerConfig.class);

    @Bean
    public DefaultErrorHandler errorHandler(KafkaTemplate<String, EnrollmentEvent> kafkaTemplate) {
        DeadLetterPublishingRecoverer recoverer =
                new DeadLetterPublishingRecoverer(kafkaTemplate, this::resolveDestination);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer);
        errorHandler.setRetryListeners((record, ex, deliveryAttempt) ->
                log.warn("Retry attempt {} for topic={}: {}", deliveryAttempt, record.topic(), ex.getMessage()));

        return errorHandler;
    }

    private TopicPartition resolveDestination(ConsumerRecord<?, ?> record, Exception ex) {
        int retryCount = KafkaUtils.getRetryCount(record);
        KafkaUtils.incrementRetryCount(record);

        if (retryCount == 0) {
            return new TopicPartition(KafkaTopics.RETRY_30S, record.partition());
        } else if (retryCount == 1) {
            return new TopicPartition(KafkaTopics.RETRY_5M, record.partition());
        } else {
            return new TopicPartition(KafkaTopics.DLQ, record.partition());
        }
    }
}
