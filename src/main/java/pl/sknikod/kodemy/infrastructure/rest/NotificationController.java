package pl.sknikod.kodemy.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.model.entity.Notification;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class NotificationController implements NotificationControllerDefinition {
    private final NotificationService notificationService;

    public Notification getNotificationById(@PathVariable Long notificationId) {
        return notificationService.getNotification(notificationId);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    public List<Notification> getNotifications() {
        return notificationService.getNotifications();
    }

    public void markAsRead(@PathVariable Long notificationId) {
        notificationService.markAsRead(notificationId);
    }
}

