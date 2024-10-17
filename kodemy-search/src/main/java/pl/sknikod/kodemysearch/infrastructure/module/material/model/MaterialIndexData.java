package pl.sknikod.kodemysearch.infrastructure.module.material.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import pl.sknikod.kodemysearch.util.data.IndexData;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MaterialIndexData extends IndexData {
    String title;
    String description;
    MaterialStatus status;
    boolean isActive;
    double avgGrade;
    MaterialIndexEvent.Author author;
    Instant createdDate;
    Long sectionId;
    Long categoryId;
    List<Tag> tags;

    public MaterialIndexData(
            Long id, String title, String description,
            MaterialStatus status, boolean isActive, double avgGrade,
            MaterialIndexEvent.Author author, Instant createdDate,
            Long sectionId, Long categoryId, List<Tag> tags
    ) {
        super(id);
        this.title = title;
        this.description = description;
        this.status = status;
        this.isActive = isActive;
        this.avgGrade = avgGrade;
        this.author = author;
        this.createdDate = createdDate;
        this.sectionId = sectionId;
        this.categoryId = categoryId;
        this.tags = tags;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        Long id;
        String username;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Tag {
        Long id;
        String name;
    }
}
