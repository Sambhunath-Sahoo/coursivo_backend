# Kafka & Event-Driven Patterns

## Architecture Position

Kafka sits between the Service layer and external side-effects (email, push, analytics).
The rule: **save to DB first, then publish the event — never the reverse.**

```
Controller → Service → Repository (save) → Producer (publish event) → return
                                              ↓
                                          Kafka Topic
                                              ↓
                                         Consumer → EmailService / NotificationService
```

The web request always completes after the DB save. The Consumer runs asynchronously.

## Event Naming

### Topics
Format: `{domain}.{entity}.{action}` — all lowercase, dots as separators.

```
course.enrollment.events          ← main topic
course.enrollment.events.retry-1  ← first retry
course.enrollment.events.retry-2  ← second retry
course.enrollment.events.dlq      ← dead letter queue
```

New domains follow the same pattern:
```
course.publish.events
user.registration.events
payment.completed.events
```

### Event Classes
- Live in `event/` package: `com.coursivo.coursivo_backend.event`
- Named: `{Action}Event` — e.g., `EnrollmentEvent`, `CoursePublishedEvent`
- Must have a no-arg constructor (required by Jackson for deserialization)
- Use `@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")` on all `LocalDateTime` fields

### Producer Classes
- Live in `kafka/` package: `com.coursivo.coursivo_backend.kafka`
- Named: `{Domain}EventProducer` — e.g., `EnrollmentEventProducer`
- Annotated with `@Component`
- Inject topic name via `@Value("${kafka.topics.enrollment}")`

### Consumer Classes
- Live in `kafka/` package alongside producers
- Named: `{Feature}Consumer` — e.g., `NotificationConsumer`
- Annotated with `@Component`
- One `@KafkaListener` per consumer method

## Producer Rules

1. **Always publish asynchronously** — use `CompletableFuture` from `kafkaTemplate.send()`; never block with `.get()`
2. **Use entity ID as the message key** — this ensures ordering within a partition for the same entity
3. **Log success and failure** — log topic, partition, and offset on success; log the error message on failure
4. **Never throw from the async callback** — log the failure, let the request complete normally

```java
public void publishEnrollmentEvent(EnrollmentEvent event) {
    String key = String.valueOf(event.getEnrollmentId());
    kafkaTemplate.send(topic, key, event)
        .whenComplete((result, ex) -> {
            if (ex != null) log.error("Failed to publish: {}", ex.getMessage());
            else log.info("Published to partition={} offset={}", ...);
        });
}
```

## Consumer Rules

1. **Use `containerFactory` explicitly** — specify `containerFactory = "enrollmentKafkaListenerContainerFactory"` to ensure the correct error handler is wired
2. **Disable auto-commit** — set `ENABLE_AUTO_COMMIT_CONFIG = false`; use `AckMode.RECORD`
3. **Keep the listener method thin** — delegate to a service (EmailService, NotificationService), do not put logic inside the listener method
4. **Never swallow exceptions** — let them propagate so the error handler can retry or DLQ

```java
@KafkaListener(
    topics = "${kafka.topics.enrollment}",
    groupId = "notification-group",
    containerFactory = "enrollmentKafkaListenerContainerFactory"
)
public void handleEnrollmentEvent(ConsumerRecord<String, EnrollmentEvent> record) {
    notificationService.processEnrollment(record.value());
}
```

## Error Handling & Retry

All retry configuration lives in `config/KafkaRetryConfig.java`.

Retry policy:
- **2 attempts** with `FixedBackOff(0L, 2L)` (immediate retry, no delay for local simplicity)
- After 3 total failures → `DeadLetterPublishingRecoverer` publishes to DLQ
- Non-retryable exceptions (e.g., `IllegalArgumentException` for bad data) — add with `handler.addNotRetryableExceptions(...)`

DLQ topic convention: original topic name + `.dlq`

```java
@Bean
public DefaultErrorHandler enrollmentErrorHandler(DeadLetterPublishingRecoverer recoverer) {
    DefaultErrorHandler handler = new DefaultErrorHandler(recoverer, new FixedBackOff(0L, 2L));
    handler.addNotRetryableExceptions(IllegalArgumentException.class);
    return handler;
}
```

## Idempotency

**Rule: every consumer that writes to the DB must check for duplicates before writing.**

Use a unique `idempotency_key` column on the target table with a DB-level unique constraint.
Format: `{NOTIFICATION_TYPE}_{entityId}` — e.g., `ENROLLMENT_STUDENT_42`.

Check pattern:
```java
if (repository.existsByIdempotencyKey(key)) {
    log.warn("Duplicate skipped: {}", key);
    return;
}
```

Never rely only on application-level checks — the DB constraint is the safety net for concurrent retries.

## Serialization

- Producer: `JsonSerializer` (Spring Kafka)
- Consumer: `JsonDeserializer` with `addTrustedPackages("com.coursivo.coursivo_backend.event")`
- All event classes must have no-arg constructors and explicit getters/setters (or Lombok)

## Configuration in `application.properties`

All topic names come from properties — never hardcoded in Java:

```properties
kafka.topics.enrollment=course.enrollment.events
kafka.topics.enrollment-retry1=course.enrollment.events.retry-1
kafka.topics.enrollment-retry2=course.enrollment.events.retry-2
kafka.topics.enrollment-dlq=course.enrollment.events.dlq
```

Inject with `@Value("${kafka.topics.enrollment}")`.

## Local Development

Run Kafka via Docker Compose at the workspace root. Kafka UI runs at `http://localhost:8090`.
Never check in real Kafka credentials — local dev always points to `localhost:9092`.

## What NOT to Do

❌ Do not call `kafkaTemplate.send(...).get()` (blocks the request thread)  
❌ Do not put business logic inside a `@KafkaListener` method (put it in a service)  
❌ Do not publish to Kafka before the DB save succeeds  
❌ Do not skip the `containerFactory` annotation — defaults won't have the error handler  
❌ Do not hardcode topic names as String literals in Java  
❌ Do not use `@SpringBootTest` for Kafka consumer unit tests — mock the service instead
