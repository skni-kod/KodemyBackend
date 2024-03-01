package pl.sknikod.kodemynotification.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sknikod.kodemynotification.infrastructure.common.entity.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
