package com.coursivo.coursivo_backend.config;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.Map;

@Configuration
public class KafkaTopicConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        KafkaAdmin admin = new KafkaAdmin(Map.of(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers));
        admin.setAutoCreate(true);
        return admin;
    }

    @Bean
    public NewTopic enrollmentMainTopic() {
        return TopicBuilder.name(KafkaTopics.MAIN).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic enrollmentRetry30sTopic() {
        return TopicBuilder.name(KafkaTopics.RETRY_30S).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic enrollmentRetry5mTopic() {
        return TopicBuilder.name(KafkaTopics.RETRY_5M).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic enrollmentDlqTopic() {
        return TopicBuilder.name(KafkaTopics.DLQ).partitions(1).replicas(1).build();
    }
}
