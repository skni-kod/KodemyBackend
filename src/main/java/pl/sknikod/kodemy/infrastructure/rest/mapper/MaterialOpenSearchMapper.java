package pl.sknikod.kodemy.infrastructure.rest.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import org.mapstruct.*;
import org.opensearch.search.SearchHit;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.model.material.Material;
import pl.sknikod.kodemy.infrastructure.rest.model.MaterialOpenSearch;

import java.util.Map;

@Mapper(componentModel = "spring")
public abstract class MaterialOpenSearchMapper {
    @Autowired
    private ObjectMapper objectMapper;

    @Mappings(value = {
            @Mapping(target = "user", source = "user.username"),
            @Mapping(target = "active", source = "active"),
            @Mapping(target = "categoryId", source = "category.id")
    })
    public abstract MaterialOpenSearch map(Material material);

    public MaterialOpenSearch map(SearchHit hit) {
        return map(hit.getSourceAsMap(), MaterialOpenSearch.class);
    }

    @ObjectFactory
    protected <T> T map(Map<String, Object> map, @TargetType Class<T> targetType) {
        return objectMapper.convertValue(map, targetType);
    }

    protected String map(Object object) {
        return Try.of(() -> objectMapper.writeValueAsString(object))
                .onFailure(ex -> {
                    throw new ServerProcessingException(ex.getMessage());
                })
                .get();
    }
}
