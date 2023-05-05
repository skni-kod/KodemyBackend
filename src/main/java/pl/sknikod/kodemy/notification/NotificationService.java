package pl.sknikod.kodemy.notification;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.general.NotFoundException;
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
                .findUsersByRoleAdmin(
                        Set.of(
                                RoleName.ROLE_SUPERADMIN.name(),
                                RoleName.ROLE_ADMIN.name()
                        )
                )
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
                .orElseThrow(() -> new NotFoundException(NotFoundException.NotFoundFormat.entityId, Notification.class,id));
    }

    @Component
    public static class NotificationExecutor {
        @Autowired
        private NotificationRepository notificationRepository;

        @RabbitListener(queues = {
                RabbitMQConfig.NOTIFICATION_USER_QUEUE_NAME,
                "q.notification.admin"
        })
        private void receiveNotification(@Payload Notification notification) {
            log.debug("Notification saved: " + notificationRepository.saveAndFlush(notification));
        }
    }
}

