package pl.sknikod.kodemy.infrastructure.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.sknikod.kodemy.infrastructure.model.entity.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
}

