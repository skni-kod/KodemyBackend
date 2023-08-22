package pl.sknikod.kodemy.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sknikod.kodemy.infrastructure.common.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
}

