package pl.sknikod.kodemybackend.infrastructure.module.material.producer;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Material;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialStatusUpdatedProducer implements Producer<MaterialStatusUpdatedProducer.Message> {
    private final StreamBridge streamBridge;

    @Override
    public void publish(Message message) {
        log.debug("Send message to materialStatusUpdated");
        streamBridge.send("materialStatusUpdated-out-0", message);
    }

    @AllArgsConstructor
    @Value
    public static class Message {
        Long id;
        Material.MaterialStatus status;
    }
}
