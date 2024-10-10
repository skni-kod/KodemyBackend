package pl.sknikod.kodemybackend.infrastructure.module.material.producer;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Material;

import java.time.Instant;
import java.time.ZoneId;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialCreatedProducer implements Producer<MaterialCreatedProducer.Message> {
    private final StreamBridge streamBridge;

    @Override
    public void publish(Message message) {
        log.debug("Send message to materialCreated");
        streamBridge.send("materialCreated-out-0", message);
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

        public static Message map(Material material, double grade) {
            final var category = material.getCategory();
            return new Message(
                    material.getId(),
                    material.getTitle(),
                    material.getDescription(),
                    material.getStatus(),
                    material.isActive(),
                    grade,
                    null,
                    material.getCreatedDate()
                            .toInstant(ZoneId.systemDefault().getRules().getOffset(material.getCreatedDate())),
                    category.getSection().getId(),
                    category.getId(),
                    material.getTags().stream().map(tag -> new Message.Tag(tag.getId(), tag.getName())).toList()
            );
        }
    }
}
