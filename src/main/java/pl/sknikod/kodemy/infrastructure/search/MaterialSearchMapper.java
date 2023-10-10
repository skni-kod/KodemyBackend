package pl.sknikod.kodemy.infrastructure.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.FieldInfo;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.opensearch.index.mapper.KeywordFieldMapper;
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
            @Mapping(target = "technologyIds", source = "technologies")
    })
    MaterialSearchObject map(Material material);

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

    default List<Long> map(Set<Technology> technologies){
        return technologies.stream().map(Technology::getId).toList();
    }
}
