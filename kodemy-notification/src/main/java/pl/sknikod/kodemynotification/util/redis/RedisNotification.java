package pl.sknikod.kodemynotification.util.redis;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Accessors(chain = true)
public class RedisNotification {
    private String id;
    private String userId;
    @Enumerated(EnumType.STRING)
    private Status status = Status.NEW;
    private Type type;
    private LocalDateTime createdAt;
    private Map<String, String> details;

    public enum Status {
        NEW,
        SEEN,
        SEEN_READ
    }

    public enum Type {
        MATERIAL_APPROVAL_REQUEST,
        MATERIAL_STATUS_CHANGE,
        ROLE_CHANGE,
        VERSION_CHANGE
    }
}