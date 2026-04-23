Add an in-app notification record and REST endpoint to the Coursivo backend.

Ask the user for:
1. What triggers the notification (e.g. "student enrolls in course")
2. Who receives the notification (student, instructor, or both)
3. What notification type string to use (e.g. `ENROLLMENT_CONFIRMATION`, `NEW_STUDENT_ENROLLED`)
4. The title and message template

Then scaffold:

## 1. Notification Entity — `model/Notification.java`

Only add this if `Notification.java` does not already exist.

```java
package com.coursivo.coursivo_backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "notifications",
    indexes = {
        @Index(name = "idx_notifications_user_id", columnList = "user_id"),
        @Index(name = "idx_notifications_is_read", columnList = "is_read"),
        @Index(name = "idx_notifications_idempotency_key", columnList = "idempotency_key", unique = true)
    }
)
public class Notification {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 50)
    private String type;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "reference_id")
    private Long referenceId;

    @Column(name = "reference_type", length = 50)
    private String referenceType;

    @Column(name = "is_read", nullable = false)
    private boolean isRead = false;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 255)
    private String idempotencyKey;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    // Add getters and setters for all fields
}
```

## 2. Repository — `repository/NotificationRepository.java`

Only add if it does not exist:

```java
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    long countByUserIdAndIsReadFalse(Long userId);
    boolean existsByIdempotencyKey(String idempotencyKey);

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.user.id = :userId")
    void markAllReadByUserId(Long userId);
}
```

## 3. Save Method in NotificationService

In `service/NotificationService.java`, add a method for this notification type:

```java
@Transactional
public void save{NotificationType}Notification(Long userId, Long referenceId,
                                                String title, String message) {
    String key = "{NOTIFICATION_TYPE}_" + referenceId;

    if (notificationRepository.existsByIdempotencyKey(key)) {
        log.warn("Duplicate notification skipped: {}", key);
        return;
    }

    User user = userRepository.findById(userId)
        .orElseThrow(() -> new RuntimeException("User not found: " + userId));

    Notification notification = new Notification();
    notification.setUser(user);
    notification.setType("{NOTIFICATION_TYPE}");
    notification.setTitle(title);
    notification.setMessage(message);
    notification.setReferenceId(referenceId);
    notification.setReferenceType("{REFERENCE_ENTITY}");
    notification.setIdempotencyKey(key);

    notificationRepository.save(notification);
}
```

## 4. Response DTO — `dto/notification/NotificationResponse.java`

```java
package com.coursivo.coursivo_backend.dto.notification;

import java.time.LocalDateTime;

public record NotificationResponse(
    Long id,
    String type,
    String title,
    String message,
    Long referenceId,
    String referenceType,
    boolean isRead,
    LocalDateTime createdAt
) {}
```

## 5. Controller — `controller/NotificationController.java`

Only add if it does not exist:

```java
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<NotificationResponse> list = notificationService
            .getNotificationsForUser(userDetails.getUser().getId())
            .stream().map(this::toResponse).toList();
        return ResponseEntity.ok(ApiResponse.success(list, "Notifications fetched"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        long count = notificationService.countUnread(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success(Map.of("count", count), "Unread count"));
    }

    @PostMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllRead(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        notificationService.markAllRead(userDetails.getUser().getId());
        return ResponseEntity.ok(ApiResponse.success(null, "Marked as read"));
    }

    private NotificationResponse toResponse(Notification n) {
        return new NotificationResponse(n.getId(), n.getType(), n.getTitle(),
            n.getMessage(), n.getReferenceId(), n.getReferenceType(),
            n.isRead(), n.getCreatedAt());
    }
}
```

## 6. Security Config

Add `/api/notifications/**` to the protected routes (requires any authenticated user):

```java
.requestMatchers("/api/notifications/**").authenticated()
```

## Verify

1. Trigger the event that creates notifications
2. `SELECT * FROM notifications ORDER BY created_at DESC LIMIT 10;` — rows should appear
3. Call `GET /api/notifications` with a valid Bearer token — response should list them
4. Call `GET /api/notifications/unread-count` — should return `{"count": N}`
5. Call `POST /api/notifications/mark-all-read` — all notifications for user flip to `is_read = true`
6. Trigger the same event again — confirm no duplicate notification row (idempotency works)
