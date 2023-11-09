package pl.sknikod.kodemybackend.infrastructure.material.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import lombok.experimental.FieldDefaults;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.rest.UserDetails;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public class SingleMaterialResponse {
    final Long id;
    final String title;
    final String description;
    final String link;
    @Enumerated(EnumType.STRING)
    final Material.MaterialStatus status;
    final TypeDetails type;
    final List<TechnologyDetails> technologies;
    @Setter
    Double averageGrade;
    @Setter
    List<Long> gradeStats;
    final AuthorDetails author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    final Date createdDate;

    @EqualsAndHashCode(callSuper = true)
    @Value
    public static class AuthorDetails extends UserDetails {
        public AuthorDetails(Long id, String username) {
            super(id, username);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class TechnologyDetails extends BaseDetails {
        public TechnologyDetails(Long id, String name) {
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
}
