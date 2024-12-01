package pl.sknikod.kodemysearch.infrastructure.module.material;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.opensearch._types.WriteResponseBase;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialStatusChangeData;
import pl.sknikod.kodemysearch.infrastructure.store.MaterialSearchStore;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialUpdateStatusService {
    private final MaterialSearchStore materialSearchStore;
    private final ObjectMapper objectMapper;

    private Try<MaterialStatusChangeData> mapToMaterialStatusChangeData(String msg) {
        return Try.of(() -> objectMapper.readValue(msg, MaterialStatusChangeData.class))
                .onFailure(ex -> log.error("Status change failed due to an exception: {}", ex.getMessage(), ex));
    }

    public String updateStatus(String msg) {
        return mapToMaterialStatusChangeData(msg)
                .flatMap(d -> materialSearchStore.update(d.getId(), d.getStatus()))
                .map(WriteResponseBase::id)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
