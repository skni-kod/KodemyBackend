package pl.sknikod.kodemysearch.infrastructure.module.material.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialAddUpdateService;

import java.util.function.Consumer;

@Slf4j
@Component("materialCreated")
@RequiredArgsConstructor
public class MaterialCreatedConsumer implements Consumer<String> {

    private final MaterialAddUpdateService materialAddUpdateService;

    @Override
    public void accept(String msg) {
        log.info("Consuming message from materialCreated");
        var id = materialAddUpdateService.index(msg);
        log.info("Material indexed ({})", id);
    }
}
