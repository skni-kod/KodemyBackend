package pl.sknikod.kodemysearch.infrastructure.module.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class MaterialRabbitConsumer {
    private final MaterialAddUpdateUseCase materialAddUpdateUseCase;

    @Bean("materialCreated")
    public Consumer<Message<String>> materialCreated() {
        return message -> {
            log.info("Consuming message from materialCreated");
            var id = materialAddUpdateUseCase.index(message);
            log.info("Material indexed ({})", id);
        };
    }

    @Bean("materialUpdated")
    public Consumer<Message<String>> materialUpdated() {
        return message -> {
            log.info("Consuming message from materialUpdated");
            var id = materialAddUpdateUseCase.reindex(message);
            log.info("Material updated ({})", id);
        };
    }
}
