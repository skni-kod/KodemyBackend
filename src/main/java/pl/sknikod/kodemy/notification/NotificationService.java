package pl.sknikod.kodemy.notification;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.user.UserPrincipal;
import pl.sknikod.kodemy.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static pl.sknikod.kodemy.config.RabbitMQConfig.NotificationQueue;

@Service
public class NotificationService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    public void sendNotification(String title, String message, Long recipientId) {
        rabbitTemplate.convertAndSend(
                NotificationQueue.EXCHANGE_NAME,
                NotificationQueue.ROUTING_KEY,
                new Notification(null, title, message, LocalDateTime.now(), false, recipientId)
        );
    }

    public void sendNotificationToAdmins(String title, String message) {
        userRepository
                .findUsersByRoleAdmin()
                .forEach(user -> rabbitTemplate.convertAndSend(
                                NotificationQueue.EXCHANGE_NAME,
                                NotificationQueue.ROUTING_KEY,
                                new Notification(
                                        null, title, message,
                                        LocalDateTime.now(), false, user.getId()
                                )
                        )
                );
    }

    public List<Notification> getNotifications() {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(UserPrincipal.getCurrentSessionUser().getId());
    }

    public void markAsRead(Long id) {
        getNotification(id);
    }

    public Notification getNotification(Long id) {
        return notificationRepository.findById(id)
                .map(notification -> {
                    notification.setRead(true);
                    return notificationRepository.save(notification);
                })
                .orElseThrow(() -> new NotFoundException(NotFoundException.NotFoundFormat.entityId, Notification.class,id));
    }
}

