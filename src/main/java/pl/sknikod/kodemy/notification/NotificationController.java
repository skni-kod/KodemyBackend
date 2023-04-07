package pl.sknikod.kodemy.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class NotificationController implements NotificationControllerDefinition {
    @Autowired
    private NotificationService notificationService;

    public Notification getNotificationById(@PathVariable Long id) {
        return notificationService.getNotification(id);
    }

    public List<Notification> getNotifications() {
        return notificationService.getNotifications();
    }

    public void markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
    }
}

