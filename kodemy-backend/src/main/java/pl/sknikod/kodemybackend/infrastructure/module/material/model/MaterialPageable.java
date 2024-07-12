package pl.sknikod.kodemybackend.infrastructure.module.material.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import lombok.experimental.FieldDefaults;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;

import java.time.LocalDateTime;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
@Builder
public class MaterialPageable {
    final Long id;
    final String title;
    final String description;
    final String link;
    @Enumerated(EnumType.STRING)
    final Material.MaterialStatus status;
    final TypeDetails type;
    final List<TagDetails> tags;
    final Double gradeAvg;
    final AuthorDetails author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    final LocalDateTime createdDate;

    @Value
    public static class AuthorDetails {
        Long id;
        String name;
    }

    @Value
    public static class TagDetails {
        Long id;
        String name;
    }

    @Value
    public static class TypeDetails {
        Long id;
        String name;
    }

    public static MaterialPageable map(Material material, Double avgGrade, String username) {
        var output = MaterialPageable.builder();
        var type = material.getType();
        output.type(new MaterialPageable.TypeDetails(
                type.getId(), type.getName()
        ));
        var tags = material.getTags()
                .stream()
                .map(tag -> new MaterialPageable.TagDetails(tag.getId(), tag.getName()))
                .toList();
        output.tags(tags);
        output.author(new MaterialPageable.AuthorDetails(material.getUserId(), username));
        output.id(material.getId());
        output.title(material.getTitle());
        output.description(material.getDescription());
        output.link(material.getLink());
        output.status(material.getStatus());
        output.createdDate(material.getCreatedDate());
        output.gradeAvg(avgGrade);
        return output.build();
    }
}

