package pl.sknikod.kodemynotification.infrastructure.database.entity;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Type;
import pl.sknikod.kodemynotification.util.data.BaseEntity;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Column(nullable = false, updatable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;

    @org.hibernate.annotations.Type(value = JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> details;

    public enum Status {
        NEW,
        SEEN,
        ERROR
    }

    public enum Type {
        MATERIAL_APPROVAL_REQUEST,
        MATERIAL_STATUS_CHANGE,
        ROLE_CHANGE,
        VERSION_CHANGE
    }
}
