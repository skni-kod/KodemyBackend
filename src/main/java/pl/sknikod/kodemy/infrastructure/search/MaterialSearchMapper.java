package pl.sknikod.kodemy.infrastructure.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.mapstruct.*;
import org.opensearch.search.SearchHit;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialOpenSearch;

import java.util.Map;

@Mapper(componentModel = "spring")
public interface MaterialSearchMapper {
    ObjectMapper objectMapper = new ObjectMapper();

    @Mappings(value = {
            @Mapping(target = "user", source = "user.username"),
            @Mapping(target = "active", source = "active"),
            @Mapping(target = "categoryId", source = "category.id")
    })
    MaterialOpenSearch map(Material material);

    default MaterialOpenSearch map(SearchHit hit) {
        return map(hit.getSourceAsMap(), MaterialOpenSearch.class);
    }

    @ObjectFactory
    default <T> T map(Map<String, Object> map, @TargetType Class<T> targetType) {
        return objectMapper.convertValue(map, targetType);
    }

    default String map(Object object) {
        return Try.of(() -> objectMapper.writeValueAsString(object))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .get();
    }
}
