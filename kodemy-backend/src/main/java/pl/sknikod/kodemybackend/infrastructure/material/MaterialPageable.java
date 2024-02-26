package pl.sknikod.kodemybackend.infrastructure.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.FieldDefaults;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
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
    final List<TechnologyDetails> technologies;
    final Double gradeAvg;
    final AuthorDetails author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    final Date createdDate;

    @Value
    public static class AuthorDetails {
        Long id;
        String name;
    }

    @Value
    public static class TechnologyDetails {
        Long id;
        String name;
    }

    @Value
    public static class TypeDetails {
        Long id;
        String name;
    }
}

