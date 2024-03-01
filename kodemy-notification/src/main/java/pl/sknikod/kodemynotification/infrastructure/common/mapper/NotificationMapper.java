package pl.sknikod.kodemynotification.infrastructure.common.mapper;

import org.mapstruct.Mapper;
import pl.sknikod.kodemynotification.infrastructure.common.entity.Notification;
import pl.sknikod.kodemynotification.infrastructure.notification.QueueConsumer;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    Notification map(QueueConsumer.NotificationEvent event);
}
