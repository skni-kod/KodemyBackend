package pl.sknikod.kodemy.notification.rabbitmq;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.config.RabbitMQConfig;
import pl.sknikod.kodemy.notification.Notification;
import pl.sknikod.kodemy.notification.NotificationRepository;

@Component
@AllArgsConstructor
public class RabbitService {

    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.NotificationQueue.QUEUE_NAME)
    private void receiveNotification(Notification notification) {
        notificationRepository.save(notification);
    }
}