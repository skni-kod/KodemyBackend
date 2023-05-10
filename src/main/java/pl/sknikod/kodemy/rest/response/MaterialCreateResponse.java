package pl.sknikod.kodemy.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.*;
import pl.sknikod.kodemy.material.MaterialStatus;
import pl.sknikod.kodemy.rest.UserDetails;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;
import java.util.List;

@Value
public class MaterialCreateResponse {
    Long id;
    String title;
    String description;
    String link;
    @Enumerated(EnumType.STRING)
    MaterialStatus status;
    UserDetails createdBy;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Europe/Warsaw")
    Date createdDate;
    TypeDetails type;
    CategoryDetails category;
    List<TechnologyDetails> technologies;

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class TypeDetails extends BaseDetails {
        public TypeDetails(Long id, String name) {
            super(id, name);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class CategoryDetails extends BaseDetails {
        public CategoryDetails(Long id, String name) {
            super(id, name);
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class TechnologyDetails extends BaseDetails {
        public TechnologyDetails(Long id, String name) {
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
