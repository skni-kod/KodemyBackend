package pl.sknikod.kodemy.notification;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.List;

@RequestMapping("/api/notification")
@Tag(name = "Notification")
@SwaggerResponse
@SwaggerResponse.SuccessCode
public interface NotificationControllerDefinition {
    @GetMapping("/{id}")
    @Operation(summary = "Get notification by ID")
    @SwaggerResponse.AuthRequest
    Notification getNotificationById(@PathVariable Long id);

    @GetMapping
    @Operation(summary = "Get all notification")
    @SwaggerResponse.AuthRequest
    List<Notification> getNotifications();

    @PutMapping("/{id}")
    @Operation(summary = "Mark notification as read")
    @SwaggerResponse.AuthRequest
    void markAsRead(@PathVariable Long id);
}
