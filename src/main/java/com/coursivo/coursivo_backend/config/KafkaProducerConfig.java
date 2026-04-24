package com.coursivo.coursivo_backend.config;

import com.coursivo.coursivo_backend.event.EnrollmentEvent;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public ProducerFactory<String, EnrollmentEvent> enrollmentProducerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ProducerConfig.ACKS_CONFIG, "all");
		props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
		props.put(ProducerConfig.RETRIES_CONFIG, 3);
		Serializer<EnrollmentEvent> valueSerializer = (topic, data) -> {
			try {
				return objectMapper.writeValueAsBytes(data);
			} catch (Exception e) {
				throw new SerializationException("Failed to serialize EnrollmentEvent", e);
			}
		};
		return new DefaultKafkaProducerFactory<>(props, new StringSerializer(), valueSerializer);
	}

	@Bean
	public KafkaTemplate<String, EnrollmentEvent> kafkaTemplate() {
		return new KafkaTemplate<>(enrollmentProducerFactory());
	}

}
