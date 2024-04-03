package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

@Component
@AllArgsConstructor
public class MaterialPageableMapper {
    MaterialPageable map(Material material, Double avgGrade) {
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
        var author = material.getAuthor();
        output.author(new MaterialPageable.AuthorDetails(
                author.getId(), author.getUsername()
        ));

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
