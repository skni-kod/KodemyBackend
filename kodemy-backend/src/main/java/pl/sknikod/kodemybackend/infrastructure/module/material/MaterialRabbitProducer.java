package pl.sknikod.kodemybackend.infrastructure.module.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialRabbitProducer {
    private final StreamBridge rabbitStream;

    public void sendNewMaterialToIndex(Message message) {
        log.debug("Send message to materialCreated");
        rabbitStream.send("materialCreated-out-0", message);
    }

    public void sendUpdatedMaterialToReindex(Message message) {
        log.debug("Send message to materialUpdated");
        rabbitStream.send("materialUpdated-out-0", message);
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdDate;
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
                    material.getCreatedDate(),
                    category.getSection().getId(),
                    category.getId(),
                    material.getTags().stream().map(tag -> new Message.Tag(tag.getId(), tag.getName())).toList()
            );
        }
    }
}
