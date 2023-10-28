package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.Data;
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
            @Mapping(target = "technologyIds", source = "technologies"),
            @Mapping(target = "avgGrade", constant = "0"),
    })
    MaterialEvent map(Material material);

    default MaterialEvent map(Material material, double grade) {
        var materialSearchObject = map(material);
        materialSearchObject.setAvgGrade(grade);
        return materialSearchObject;
    }

    @Data
    class MaterialEvent {
        private Long id;
        private String title;
        private String description;
        private String link;
        private Material.MaterialStatus status;
        private boolean isActive;
        private double avgGrade;
        private String user;
        private Date createdDate;
        private Long sectionId;
        private Long categoryId;
        private List<Long> technologyIds;
    }
}
