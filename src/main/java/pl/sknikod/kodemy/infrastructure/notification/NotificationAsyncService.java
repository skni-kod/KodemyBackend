package pl.sknikod.kodemy.infrastructure.notification;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.configuration.RabbitConfig;
import pl.sknikod.kodemy.infrastructure.common.entity.Notification;
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName;
import pl.sknikod.kodemy.infrastructure.common.repository.UserRepository;

import java.util.Set;

@Component
@AllArgsConstructor
public class NotificationAsyncService {
    private final RabbitTemplate rabbitTemplate;
    private final UserRepository userRepository;

    public void send(String title, String message, Long recipientId) {
        rabbitTemplate.convertAndSend(
                NotificationConfig.NAME + RabbitConfig.EXCHANGE_SUFFIX,
                "user",
                new Notification(title, message, recipientId)
        );
    }

    public void sendToAdmins(String title, String message) {
        userRepository
                .findUsersByRoleAdmin(Set.of(
                        RoleName.ROLE_SUPERADMIN.name(),
                        RoleName.ROLE_ADMIN.name()
                ))
                .forEach(user -> send(title, message, user.getId()));
    }
}
