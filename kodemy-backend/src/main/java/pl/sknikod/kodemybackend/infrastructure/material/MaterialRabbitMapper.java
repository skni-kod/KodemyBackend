package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MaterialRabbitMapper {
    @Mappings(value = {
            @Mapping(target = "author", source = "author.name"),
            @Mapping(target = "isActive", source = "active"),
            @Mapping(target = "sectionId", source = "category.section.id"),
            @Mapping(target = "categoryId", source = "category.id"),
            @Mapping(target = "avgGrade", constant = "0.00"),
    })
    MaterialEvent map(Material material);

    default MaterialEvent map(Material material, double grade) {
        var event = map(material);
        event.setAvgGrade(grade);
        return event;
    }

    default NotificationEvent map(Material material, Long recipientId) {
        var event = new NotificationEvent();
        event.setType(NotificationEvent.NotificationType.MATERIAL_STATUS_CHANGE);
        event.setRecipientId(recipientId);
        var additionalData = new NotificationDataJsonB(recipientId, material.getId(), material.getStatus().toString(), "e", "e");
        event.setAdditionalData(additionalData);
        return event;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class MaterialEvent {
        private Long id;
        private String title;
        private String description;
        private String link;
        private Material.MaterialStatus status;
        private boolean isActive;
        private double avgGrade;
        private String author;
        private Date createdDate;
        private Long sectionId;
        private Long categoryId;
        private List<Technology> technologies;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Technology {
            private Long id;
            private String name;
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    class NotificationEvent {
        private Long id;
        private LocalDateTime createdAt;
        private boolean isRead;
        private Long recipientId;
        private NotificationType type;
        private NotificationDataJsonB additionalData;

        public enum NotificationType {
            MATERIAL_STATUS_CHANGE,
            MATERIAL_APPROVAL_REQUEST,
            ROLE_CHANGE,
            VERSION_CHANGE,
        }
    }

    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    class NotificationDataJsonB {
        Long authorId;
        Long materialId;
        String status;
        String role;
        String version;
    }
}
