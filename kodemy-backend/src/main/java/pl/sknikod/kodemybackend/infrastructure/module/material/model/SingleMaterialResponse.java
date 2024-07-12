package pl.sknikod.kodemybackend.infrastructure.module.material.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import pl.sknikod.kodemybackend.infrastructure.common.model.UserDetails;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;

import java.time.LocalDateTime;
import java.util.List;

@Value
public class SingleMaterialResponse {
    Long id;
    String title;
    String description;
    String link;
    @Enumerated(EnumType.STRING)
    Material.MaterialStatus status;
    TypeDetails type;
    List<TagDetails> tags;
    Double averageGrade;
    List<Long> gradeStats;
    AuthorDetails author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    LocalDateTime createdDate;

    @EqualsAndHashCode(callSuper = true)
    @Value
    public static class AuthorDetails extends UserDetails {
        public AuthorDetails(Long id, String username) {
            super(id, username);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class TagDetails extends BaseDetails {
        public TagDetails(Long id, String name) {
            super(id, name);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class TypeDetails extends BaseDetails {
        public TypeDetails(Long id, String name) {
            super(id, name);
        }
    }

    @Getter
    @AllArgsConstructor
    @ToString
    @EqualsAndHashCode
    @Hidden
    private static class BaseDetails {
        Long id;
        String name;
    }

    public static SingleMaterialResponse map(Material material, Double averageGrade, List<Long> gradeStats, String userUsername) {
        return new SingleMaterialResponse(
                material.getId(),
                material.getTitle(),
                material.getDescription(),
                material.getLink(),
                material.getStatus(),
                new TypeDetails(material.getType().getId(), material.getType().getName()),
                material.getTags().stream().map(tag -> new TagDetails(tag.getId(), tag.getName())).toList(),
                averageGrade,
                gradeStats,
                new AuthorDetails(material.getUserId(), userUsername),
                material.getCreatedDate()
        );
    }
}
