package pl.sknikod.kodemybackend.infrastructure.event.producer;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.event.Producer;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialGradeUpdatedProducer implements Producer<MaterialGradeUpdatedProducer.Message> {
    private final StreamBridge streamBridge;

    @Override
    public void publish(Message message) {
        log.debug("Send message to materialGradeUpdated");
        streamBridge.send("materialGradeUpdated-out-0", message);
    }

    @AllArgsConstructor
    @Value
    public static class Message {
        Long id;
        double avgGrade;
    }
}
