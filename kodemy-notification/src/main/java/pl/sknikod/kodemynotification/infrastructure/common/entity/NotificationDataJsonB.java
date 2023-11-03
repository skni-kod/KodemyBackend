package pl.sknikod.kodemynotification.infrastructure.common.entity;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDataJsonB {
    Long authorId;
    Long materialId;
    String status;
    String role;
    String version;
}
