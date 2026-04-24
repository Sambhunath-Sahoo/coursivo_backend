package com.coursivo.coursivo_backend.config;

import com.coursivo.coursivo_backend.event.EnrollmentEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

	@Value("${spring.kafka.bootstrap-servers}")
	private String bootstrapServers;

	@Autowired
	private ObjectMapper objectMapper;

	@Bean
	public ConsumerFactory<String, EnrollmentEvent> enrollmentConsumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, "notification-group");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

		Deserializer<EnrollmentEvent> valueDeserializer = (topic, data) -> {
			try {
				return objectMapper.readValue(data, EnrollmentEvent.class);
			} catch (Exception e) {
				throw new SerializationException("Failed to deserialize EnrollmentEvent", e);
			}
		};
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, EnrollmentEvent> enrollmentKafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, EnrollmentEvent> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(enrollmentConsumerFactory());
		return factory;
	}

}
