package pl.sknikod.kodemy.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.config.RabbitMQConfig;
import pl.sknikod.kodemy.exception.general.NotFoundException;
import pl.sknikod.kodemy.user.UserPrincipal;
import pl.sknikod.kodemy.user.UserRepository;

import java.util.List;

@Service
@Slf4j
public class NotificationService {
    @Autowired
    private AmqpTemplate rabbitTemplate;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @RabbitListener(queues = {
            RabbitMQConfig.NOTIFICATION_USER_QUEUE_NAME, RabbitMQConfig.NOTIFICATION_USER_QUEUE_NAME
    })
    private void receiveNotification(@Payload Notification notification) {
        log.debug("Notification saved: " + notificationRepository.save(notification));
    }

    public void sendNotification(String title, String message, Long recipientId) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.NOTIFICATION_EXCHANGE_NAME,
                RabbitMQConfig.NOTIFICATION_USER_KEY,
                new Notification(title, message, recipientId)
        );
    }

    public void sendNotificationToAdmins(String title, String message) {
        userRepository
                .findUsersByRoleAdmin()
                .forEach(user -> rabbitTemplate.convertAndSend(
                                RabbitMQConfig.NOTIFICATION_EXCHANGE_NAME,
                                RabbitMQConfig.NOTIFICATION_ADMIN_KEY,
                                new Notification(title, message, user.getId())
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
                .orElseThrow(() -> new NotFoundException("Notification not found"));
    }
}

