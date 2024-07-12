package pl.sknikod.kodemysearch.infrastructure.module.material.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class MaterialIndexEvent {
    Long id;
    String title;
    String description;
    String status;
    boolean isActive;
    double avgGrade;
    User user;
    LocalDateTime createdDate;
    Long sectionId;
    Long categoryId;
    List<Tag> tags;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class User {
        Long id;
        String username;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class Tag {
        Long id;
        String name;
    }
}