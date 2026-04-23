Add a Kafka consumer (@KafkaListener) with retry + DLQ to the Coursivo backend.

Ask the user for:
1. The topic to consume from (e.g. `course.enrollment.events`)
2. The event class it processes (e.g. `EnrollmentEvent`)
3. What the consumer should do (e.g. "send email + save in-app notification")
4. Consumer group ID (default: `notification-group`)
5. Whether idempotency is needed (answer: always yes for DB writes)

Then scaffold in this order:

## 1. Consumer — `kafka/{Feature}Consumer.java`

```java
package com.coursivo.coursivo_backend.kafka;

import com.coursivo.coursivo_backend.event.{Action}Event;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class {Feature}Consumer {

    private static final Logger log = LoggerFactory.getLogger({Feature}Consumer.class);

    // Inject the services that do the actual work
    private final {Feature}Service {feature}Service;

    public {Feature}Consumer({Feature}Service {feature}Service) {
        this.{feature}Service = {feature}Service;
    }

    @KafkaListener(
        topics = "${kafka.topics.{domain}}",
        groupId = "{consumer-group-id}",
        containerFactory = "{domain}KafkaListenerContainerFactory"
    )
    public void handle{Action}Event(ConsumerRecord<String, {Action}Event> record) {
        {Action}Event event = record.value();
        log.info("Received {action} event: {entity}Id={}", event.get{Entity}Id());

        // Delegate to services — keep this method thin
        // Exceptions propagate up so the error handler can retry or DLQ
        {feature}Service.process{Action}(event);
    }
}
```

Rules:
- The listener method must be thin — no inline business logic
- Let exceptions propagate — do NOT catch them here
- `containerFactory` must match the bean name in `KafkaConsumerConfig`

## 2. Consumer Config — `config/KafkaConsumerConfig.java`

Add (or extend if already exists) a `ConsumerFactory` and `ConcurrentKafkaListenerContainerFactory` for this event type:

```java
@Bean
public ConsumerFactory<String, {Action}Event> {domain}ConsumerFactory() {
    JsonDeserializer<{Action}Event> deserializer =
        new JsonDeserializer<>({Action}Event.class, false);
    deserializer.addTrustedPackages("com.coursivo.coursivo_backend.event");

    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "{consumer-group-id}");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
}

@Bean
public ConcurrentKafkaListenerContainerFactory<String, {Action}Event>
{domain}KafkaListenerContainerFactory(
        ConsumerFactory<String, {Action}Event> {domain}ConsumerFactory,
        DefaultErrorHandler {domain}ErrorHandler) {

    ConcurrentKafkaListenerContainerFactory<String, {Action}Event> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory({domain}ConsumerFactory);
    factory.setCommonErrorHandler({domain}ErrorHandler);
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
    return factory;
}
```

## 3. Retry + DLQ Config — `config/KafkaRetryConfig.java`

Add (or extend if already exists):

```java
@Bean
public DeadLetterPublishingRecoverer {domain}DlqRecoverer(
        KafkaTemplate<String, {Action}Event> template) {
    return new DeadLetterPublishingRecoverer(template,
        (record, ex) -> new TopicPartition("{domain}.{entity}.events.dlq", 0));
}

@Bean
public DefaultErrorHandler {domain}ErrorHandler(
        DeadLetterPublishingRecoverer {domain}DlqRecoverer) {
    DefaultErrorHandler handler = new DefaultErrorHandler({domain}DlqRecoverer,
        new FixedBackOff(0L, 2L));  // 2 retries, immediate
    handler.addNotRetryableExceptions(IllegalArgumentException.class);
    return handler;
}
```

## 4. Add Topic Names to `application.properties`

```properties
kafka.topics.{domain}={domain}.{entity}.events
kafka.topics.{domain}-dlq={domain}.{entity}.events.dlq
```

## 5. Create Kafka Topics

```bash
# Main topic
docker exec coursivo-kafka kafka-topics \
  --bootstrap-server localhost:9092 --create \
  --topic {domain}.{entity}.events \
  --partitions 3 --replication-factor 1

# DLQ topic
docker exec coursivo-kafka kafka-topics \
  --bootstrap-server localhost:9092 --create \
  --topic {domain}.{entity}.events.dlq \
  --partitions 1 --replication-factor 1
```

## 6. Idempotency in the Service

In the service method called by the consumer, always check before writing:

```java
String key = "{NOTIFICATION_TYPE}_" + event.get{Entity}Id();
if (repository.existsByIdempotencyKey(key)) {
    log.warn("Duplicate event skipped: {}", key);
    return;
}
// proceed with save
```

The target table must have a unique constraint on `idempotency_key`.

## Verify

1. Restart the backend
2. Trigger the event (e.g. enroll a student)
3. Logs should show:
   - Producer: `Published ... event`
   - Consumer: `Received ... event`
4. **Test retry:** temporarily throw `new RuntimeException("test")` in the service, trigger the event, watch it log 3 times, then check Kafka UI → DLQ topic for the dead-lettered message
5. **Test idempotency:** replay the event from Kafka UI → confirm no duplicate DB row

Follow all patterns in `.claude/rules/kafka-patterns.md`.
