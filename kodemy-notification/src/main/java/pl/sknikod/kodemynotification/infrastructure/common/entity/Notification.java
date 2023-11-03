package pl.sknikod.kodemynotification.infrastructure.common.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Data;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "notifications")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private LocalDateTime createdAt;
    private boolean isRead;
    private Long recipientId;
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    @org.hibernate.annotations.Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private NotificationDataJsonB additionalData;

    public enum NotificationType {
        MATERIAL_STATUS_CHANGE,
        MATERIAL_APPROVAL_REQUEST,
        ROLE_CHANGE,
        VERSION_CHANGE,
    }
}
