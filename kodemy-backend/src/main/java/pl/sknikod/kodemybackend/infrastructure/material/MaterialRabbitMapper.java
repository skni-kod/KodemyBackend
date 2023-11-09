package pl.sknikod.kodemybackend.infrastructure.material;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

import java.util.Date;
import java.util.List;

@Mapper(componentModel = "spring")
public interface MaterialRabbitMapper {
    @Mappings(value = {
            @Mapping(target = "isActive", source = "active"),
            @Mapping(target = "sectionId", source = "category.section.id"),
            @Mapping(target = "categoryId", source = "category.id"),
            @Mapping(target = "avgGrade", constant = "0.00"),
    })
    MaterialEvent map(Material material);

    default MaterialEvent map(Material material, double grade) {
        var event = map(material);
        event.setAvgGrade(grade);
        return event;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    class MaterialEvent {
        private Long id;
        private String title;
        private String description;
        private Material.MaterialStatus status;
        private boolean isActive;
        private double avgGrade;
        private Author author;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private Date createdDate;
        private Long sectionId;
        private Long categoryId;
        private List<Technology> technologies;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Technology {
            private Long id;
            private String name;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        public static class Author {
            private Long id;
            private String username;
        }
    }
}
