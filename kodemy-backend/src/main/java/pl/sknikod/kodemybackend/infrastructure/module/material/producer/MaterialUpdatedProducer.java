package pl.sknikod.kodemybackend.infrastructure.module.material.producer;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Material;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialUpdatedProducer implements Producer<MaterialUpdatedProducer.Message> {
    private final StreamBridge streamBridge;

    @Override
    public void publish(Message message) {
        log.debug("Send message to materialUpdated");
        streamBridge.send("materialUpdated-out-0", message);
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @Value
    public static class Message {
        Long id;
        String title;
        String description;
        Material.MaterialStatus status;
        boolean isActive;
        double avgGrade;
        Author author;
        Instant createdDate;
        Long sectionId;
        Long categoryId;
        List<Tag> tags;

        @Getter
        @Setter
        @AllArgsConstructor
        @Value
        public static class Tag {
            Long id;
            String name;
        }

        @Getter
        @Setter
        @AllArgsConstructor
        @Value
        public static class Author {
            Long id;
            String username;
        }

        public static Message map(Material material, double grade, Author author) {
            final var category = material.getCategory();
            return new Message(
                    material.getId(),
                    material.getTitle(),
                    material.getDescription(),
                    material.getStatus(),
                    material.isActive(),
                    grade,
                    author,
                    material.getCreatedDate().toInstant(ZoneId.systemDefault().getRules().getOffset(material.getCreatedDate())),
                    category.getSection().getId(),
                    category.getId(),
                    material.getTags().stream().map(tag -> new Message.Tag(tag.getId(), tag.getName())).toList()
            );
        }
    }
}
