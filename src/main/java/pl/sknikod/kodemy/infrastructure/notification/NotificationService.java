package pl.sknikod.kodemy.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.configuration.RabbitMQConfig;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.infrastructure.common.entity.Notification;
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName;
import pl.sknikod.kodemy.infrastructure.common.repository.NotificationRepository;
import pl.sknikod.kodemy.infrastructure.common.repository.UserRepository;
import pl.sknikod.kodemy.infrastructure.user.UserService;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final RabbitTemplate rabbitTemplate;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

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
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(UserService.getContextUserPrincipal().getId());
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
                .orElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Notification.class, id));
    }

    @Component
    @RequiredArgsConstructor
    public static class NotificationExecutor {
        private final NotificationRepository notificationRepository;

        @RabbitListener(queues = {
                RabbitMQConfig.NOTIFICATION_USER_QUEUE_NAME,
                "q.notification.admin"
        })
        private void receiveNotification(@Payload Notification notification) {
            log.debug("Notification saved: " + notificationRepository.saveAndFlush(notification));
        }
    }
}

