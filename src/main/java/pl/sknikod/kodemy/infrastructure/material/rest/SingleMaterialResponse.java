package pl.sknikod.kodemy.infrastructure.material.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import pl.sknikod.kodemy.infrastructure.common.entity.MaterialStatus;
import pl.sknikod.kodemy.infrastructure.common.rest.UserDetails;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

@Value
public class SingleMaterialResponse {
    Long id;
    String title;
    String description;
    String link;
    @Enumerated(EnumType.STRING)
    MaterialStatus status;
    TypeDetails type;
    List<TechnologyDetails> technologies;
    Double averageGrade;
    UserResponse creator;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Europe/Warsaw")
    Date createdDate;

    @EqualsAndHashCode(callSuper = true)
    @Value
    public static class UserResponse extends UserDetails {
        public UserResponse(Long id, String username) {
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
