package pl.sknikod.kodemy.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.configuration.RabbitConfig;
import pl.sknikod.kodemy.infrastructure.common.entity.Notification;
import pl.sknikod.kodemy.infrastructure.common.repository.NotificationRepository;

@Configuration
@Slf4j
public class NotificationConfig {
    public static final String NAME = "notification.user";

    @Bean
    public Queue notificationQueue() {
        return new Queue(NAME, true);
    }

    @Bean
    public DirectExchange notificationExchange() {
        return new DirectExchange(NAME + RabbitConfig.EXCHANGE_SUFFIX);
    }

    @Bean
    Binding notificationBinding(Queue notificationQueue, DirectExchange notificationExchange) {
        return BindingBuilder.bind(notificationQueue).to(notificationExchange).with("user");
    }

    @Component
    @RequiredArgsConstructor
    public static class Executor {
        private final NotificationRepository notificationRepository;

        @RabbitListener(queues = NAME)
        private void receive(@Payload Notification notification) {
            log.debug("Notification saved: " + notificationRepository.saveAndFlush(notification));
        }
    }
}
