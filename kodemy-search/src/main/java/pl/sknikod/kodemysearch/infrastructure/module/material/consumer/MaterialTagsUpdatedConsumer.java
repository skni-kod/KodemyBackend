package pl.sknikod.kodemysearch.infrastructure.module.material.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component("materialTagsUpdated")
@RequiredArgsConstructor
public class MaterialTagsUpdatedConsumer implements Consumer<String> {
    
    @Override
    public void accept(String msg) {
        log.info("Consuming message from materialTagsUpdated");
    }
}
