package pl.sknikod.kodemynotification.infrastructure.database.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemynotification.infrastructure.database.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
}