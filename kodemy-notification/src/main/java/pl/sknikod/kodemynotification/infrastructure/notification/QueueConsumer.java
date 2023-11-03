package pl.sknikod.kodemynotification.infrastructure.notification;

import lombok.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@DependsOn("rabbitConfig")
public class QueueConsumer {
    private final NotificationService notificationService;

    @RabbitListener(queues = "notification")
    private void materialCreated(@Payload NotificationEvent notification) {
        notificationService.materialCreated(notification);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NotificationEvent {
        private Long id;
        private LocalDateTime createdAt;
        private boolean isRead;
        private Long recipientId;
        private NotificationType type;
        private NotificationDataJsonB additionalData;

        public enum NotificationType {
            MATERIAL_STATUS_CHANGE,
            MATERIAL_APPROVAL_REQUEST,
            ROLE_CHANGE,
            VERSION_CHANGE,
        }
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class NotificationDataJsonB {
        Long authorId;
        Long materialId;
        String status;
        String role;
        String version;
    }
}