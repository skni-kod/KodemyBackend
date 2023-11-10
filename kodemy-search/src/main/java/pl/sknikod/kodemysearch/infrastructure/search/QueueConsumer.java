package pl.sknikod.kodemysearch.infrastructure.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class MaterialEvent {
        private Long id;
        private String title;
        private String description;
        private MaterialStatus status;
        private boolean isActive;
        private double avgGrade;
        private AuthorDetails author;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date createdDate;
        private Long sectionId;
        private Long categoryId;
        private List<TechnologyDetails> technologies;
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
        public static class TechnologyDetails {
            private Long id;
            private String name;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class AuthorDetails {
            private Long id;
            private String username;
        }
    }
}
