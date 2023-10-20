package pl.sknikod.kodemy.infrastructure.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.opensearch.search.SearchHit;
import org.opensearch.search.SearchHits;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.common.entity.Technology;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

import java.util.List;
import java.util.Set;
import java.util.stream.StreamSupport;

@Mapper(componentModel = "spring")
public interface MaterialSearchMapper {
    ObjectMapper objectMapper = new ObjectMapper();

    @Mappings(value = {
            @Mapping(target = "user", source = "user.username"),
            @Mapping(target = "isActive", source = "active"),
            @Mapping(target = "sectionId", source = "category.section.id"),
            @Mapping(target = "categoryId", source = "category.id"),
            @Mapping(target = "avgGrade", constant = "0.00"),
    })
    MaterialSearchObject map(Material material);

    default MaterialSearchObject map(Material material, double grade) {
        MaterialSearchObject materialSearchObject = map(material);
        materialSearchObject.setAvgGrade(grade);
        return materialSearchObject;
    }

    default List<MaterialSearchObject> map(SearchHits hits) {
        return StreamSupport.stream(hits.spliterator(), false)
                .map(SearchHit::getSourceAsString)
                .map(this::map)
                .toList();
    }

    private MaterialSearchObject map(String source) {
        return Try.of(() -> objectMapper.readValue(source, MaterialSearchObject.class))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .get();
    }
}
