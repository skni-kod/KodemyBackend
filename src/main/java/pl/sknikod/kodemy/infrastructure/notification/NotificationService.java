package pl.sknikod.kodemy.infrastructure.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.infrastructure.common.entity.Notification;
import pl.sknikod.kodemy.infrastructure.common.repository.NotificationRepository;
import pl.sknikod.kodemy.infrastructure.user.UserService;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;


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
}

