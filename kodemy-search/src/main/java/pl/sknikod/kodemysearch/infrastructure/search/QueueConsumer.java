package pl.sknikod.kodemysearch.infrastructure.search;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.infrastructure.material.MaterialStatus;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
@DependsOn("rabbitConfig")
public class QueueConsumer {

    @RabbitListener(queues = "material.created")
    private void index(@Payload MaterialEvent material) {
        System.out.println("Received message from 'material.create': " + material.toString());
    }

    @RabbitListener(queues = "material.updated")
    private void reindex(@Payload MaterialEvent material) {
        System.out.println("Received message from 'material.create': " + material.toString());
    }

    @Data
    public static class MaterialEvent {
        private Long id;
        private String title;
        private String description;
        private String link;
        private MaterialStatus status;
        private boolean isActive;
        private double avgGrade;
        private String user;
        private Date createdDate;
        private Long sectionId;
        private Long categoryId;
        private List<Long> technologyIds;
    }
}
