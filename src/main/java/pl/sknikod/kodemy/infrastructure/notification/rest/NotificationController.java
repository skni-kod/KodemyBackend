package pl.sknikod.kodemy.infrastructure.notification.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.common.entity.Notification;
import pl.sknikod.kodemy.infrastructure.notification.NotificationService;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDefinition {
    private final NotificationService notificationService;

    public Notification getNotificationById(@PathVariable Long notificationId) {
        return notificationService.getNotification(notificationId);
    }

    @PreAuthorize("hasAuthority('CAN_READ_NOTIFICATIONS')")
    public List<Notification> getNotifications() {
        return notificationService.getNotifications();
    }

    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
    }
}

