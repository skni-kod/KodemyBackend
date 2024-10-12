package pl.sknikod.kodemysearch.infrastructure.module.material.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialAddUpdateService;

import java.util.function.Consumer;

@Slf4j
@Component("materialUpdated")
@RequiredArgsConstructor
public class MaterialUpdatedConsumer implements Consumer<String> {

    private final MaterialAddUpdateService materialAddUpdateService;

    @Override
    public void accept(String msg) {
        log.info("Consuming message from materialUpdated");
        var id = materialAddUpdateService.reindex(msg);
        log.info("Material updated ({})", id);
    }
}
