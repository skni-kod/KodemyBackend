package pl.sknikod.kodemysearch.infrastructure.search;

import lombok.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@DependsOn("rabbitConfig")
public class QueueConsumer {
    private final SearchService searchService;

    @RabbitListener(queues = "material.created")
    private void index(@Payload MaterialEvent material) {
        searchService.indexMaterial(material);
    }

    @RabbitListener(queues = "material.updated")
    private void reindex(@Payload MaterialEvent material) {
        searchService.reindexMaterial(String.valueOf(material.getId()), material);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialEvent {
        private Long id;
        private String title;
        private String description;
        private String link;
        private MaterialStatus status;
        private boolean isActive;
        private double avgGrade;
        private String author;
        private Date createdDate;
        private Long sectionId;
        private Long categoryId;
        private List<Technology> technologies;

        public enum MaterialStatus {
            APPROVED, //CONFIRMED
            PENDING, //UNCONFIRMED, AWAITING_APPROVAL, PENDING
            REJECTED,
            EDITED, //CORRECTED
            BANNED
        }

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
}
