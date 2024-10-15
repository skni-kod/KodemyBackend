package pl.sknikod.kodemysearch.infrastructure.module.material.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialUpdateStatusService;

import java.util.function.Consumer;

@Slf4j
@Component("materialStatusUpdated")
@RequiredArgsConstructor
public class MaterialStatusUpdatedConsumer implements Consumer<String> {
    private final MaterialUpdateStatusService materialUpdateStatusService;
    
    @Override
    public void accept(String msg) {
        log.info("Consuming message from materialStatusUpdated");
        var id = materialUpdateStatusService.updateStatus(msg);
        log.info("Material status updated ({})", id);
    }
}
