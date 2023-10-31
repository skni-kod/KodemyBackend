package pl.sknikod.kodemynotification.infrastructure.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class NotificationDataJsonB {
    Long authorId;
    Long materialId;
    String status;
    String role;
    String version;
}
