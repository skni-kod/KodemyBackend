package pl.sknikod.kodemysearch.infrastructure.module.material;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.opensearch.client.opensearch._types.WriteResponseBase;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemysearch.infrastructure.dao.MaterialSearchDao;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexData;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexEvent;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn("rabbitConfiguration")
public class MaterialAddUpdateService {
    private final MaterialSearchDao materialSearchDao;
    private final ObjectMapper objectMapper;
    private final MaterialIndexDataMapper mapper;

    private Try<MaterialIndexData> mapToMaterialIndexData(String msg) {
        return Try.of(() -> objectMapper.readValue(msg, MaterialIndexEvent.class))
                .onFailure(ex -> log.error("Indexing failed due to an exception: {}", ex.getMessage(), ex))
                .map(mapper::map);
    }

    public String index(String msg) {
        return mapToMaterialIndexData(msg)
                .flatMap(materialSearchDao::save)
                .map(WriteResponseBase::id)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    public String reindex(String msg) {
        return mapToMaterialIndexData(msg)
                .flatMap(materialSearchDao::update)
                .map(WriteResponseBase::id)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface MaterialIndexDataMapper {
        MaterialIndexData map(MaterialIndexEvent event);
    }
}
