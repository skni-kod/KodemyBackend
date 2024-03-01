package pl.sknikod.kodemynotification.infrastructure.notification;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemynotification.exception.structure.ServerProcessingException;
import pl.sknikod.kodemynotification.infrastructure.common.entity.Notification;
import pl.sknikod.kodemynotification.infrastructure.common.mapper.NotificationMapper;
import pl.sknikod.kodemynotification.infrastructure.common.repository.NotificationRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;

    public void materialCreated(QueueConsumer.NotificationEvent notificationEvent) {
        Option.of(notificationEvent)
                .map(notificationMapper::map)
                .map(notificationRepository::save)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Notification.class));
    }
}
