Add a Kafka event producer to the Coursivo backend following the event-driven patterns in `.claude/rules/kafka-patterns.md`.

Ask the user for:
1. Domain name (e.g. "enrollment", "payment", "course")
2. What triggers the event (e.g. "when a student enrolls", "when a course is published")
3. What data the event needs to carry (fields + types)
4. The topic name (or derive it from domain: `{domain}.{entity}.events`)

Then scaffold in this order:

## 1. Event Class — `event/{Action}Event.java`

```java
package com.coursivo.coursivo_backend.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class {Action}Event {

    // Carry all data consumers need — avoid forcing consumers to re-query the DB
    private Long {entity}Id;
    private String relevantField;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime occurredAt;

    // No-arg constructor required by Jackson
    public {Action}Event() {}

    // All-args constructor
    public {Action}Event(Long {entity}Id, String relevantField, LocalDateTime occurredAt) {
        this.{entity}Id = {entity}Id;
        this.relevantField = relevantField;
        this.occurredAt = occurredAt;
    }

    // Getters and setters for every field
}
```

Rules for the event class:
- No-arg constructor is required — Jackson needs it for deserialization
- `@JsonFormat` on all `LocalDateTime` fields
- Carry enough data so consumers don't have to re-query the DB
- Never put business logic in the event class

## 2. Producer — `kafka/{Domain}EventProducer.java`

```java
package com.coursivo.coursivo_backend.kafka;

import com.coursivo.coursivo_backend.event.{Action}Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class {Domain}EventProducer {

    private static final Logger log = LoggerFactory.getLogger({Domain}EventProducer.class);

    private final KafkaTemplate<String, {Action}Event> kafkaTemplate;

    @Value("${kafka.topics.{domain}}")
    private String topic;

    public {Domain}EventProducer(KafkaTemplate<String, {Action}Event> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish{Action}Event({Action}Event event) {
        String key = String.valueOf(event.get{Entity}Id());

        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish {action} event for {entity}Id={}: {}",
                        event.get{Entity}Id(), ex.getMessage());
                } else {
                    log.info("Published {action} event: {entity}Id={}, partition={}, offset={}",
                        event.get{Entity}Id(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
                }
            });
    }
}
```

## 3. Wire into the triggering Service

In the service method where the event should be published (always **after** the DB save):

```java
// Inject the producer
private final {Domain}EventProducer {domain}EventProducer;

// After entity.save():
{Action}Event event = new {Action}Event(
    savedEntity.getId(),
    savedEntity.getRelevantField(),
    LocalDateTime.now()
);
{domain}EventProducer.publish{Action}Event(event);
```

## 4. Add topic name to `application.properties`

```properties
kafka.topics.{domain}={domain}.{entity}.events
```

## 5. Add `KafkaTemplate` bean in `KafkaRetryConfig.java`

If no `KafkaTemplate<String, {Action}Event>` bean exists yet, add:

```java
@Bean
public ProducerFactory<String, {Action}Event> {domain}ProducerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);
    props.put(ProducerConfig.ACKS_CONFIG, "all");
    return new DefaultKafkaProducerFactory<>(props);
}

@Bean
public KafkaTemplate<String, {Action}Event> {domain}KafkaTemplate() {
    return new KafkaTemplate<>({domain}ProducerFactory());
}
```

## Verify

1. Restart the backend
2. Trigger the action (e.g. enroll a student)
3. Check logs for: `Published {action} event: ... partition=... offset=...`
4. Open Kafka UI at `http://localhost:8090` → Topics → your topic → Messages
5. Confirm the JSON event appears with correct fields

Follow all Kafka rules in `.claude/rules/kafka-patterns.md`.
