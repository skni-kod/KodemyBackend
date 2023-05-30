package pl.sknikod.kodemy.infrastructure.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.sknikod.kodemy.infrastructure.model.notification.Notification;
import pl.sknikod.kodemy.util.SwaggerResponse;

import java.util.List;

@RequestMapping("/api/notification")
@Tag(name = "Notification")
@SwaggerResponse
@SwaggerResponse.SuccessCode
@SwaggerResponse.AuthRequest
public interface NotificationControllerDefinition {
    @GetMapping("/{id}")
     @Operation(summary = "Show Notification by ID")
    @SwaggerResponse.ReadRequest
    Notification getNotificationById(@PathVariable Long id);

    @GetMapping
    @Operation(summary = "Show all Notifications")
    List<Notification> getNotifications();

    @PutMapping("/{id}")
    @SwaggerResponse.NotFoundCode
    @Operation(summary = "Mark Notification as read")
    void markAsRead(@PathVariable Long id);
}