package pl.sknikod.kodemysearch.infrastructure.module.material;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.opensearch.client.opensearch._types.WriteResponseBase;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.exception.ExceptionUtil;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexEvent;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialIndexData;

@Slf4j
@Component
@RequiredArgsConstructor
@DependsOn("rabbitConfig")
public class MaterialAddUpdateUseCase {
    private final MaterialSearchHandler materialSearchHandler;
    private final ObjectMapper objectMapper;
    private final MaterialIndexDataMapper mapper;

    private Try<MaterialIndexData> mapToMaterialIndexData(Message<String> message){
        return Try.of(() -> objectMapper.readValue(message.getPayload(), MaterialIndexEvent.class))
                .onFailure(ex -> log.error("Indexing failed due to an exception: {}", ex.getMessage(), ex))
                .map(mapper::map);
    }

    public String index(Message<String> message){
        return mapToMaterialIndexData(message)
                .flatMap(materialSearchHandler::save)
                .map(WriteResponseBase::id)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    public String reindex(Message<String> message){
        return mapToMaterialIndexData(message)
                .flatMap(materialSearchHandler::update)
                .map(WriteResponseBase::id)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    @Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
    public interface MaterialIndexDataMapper {
        MaterialIndexData map(MaterialIndexEvent event);
    }
}
