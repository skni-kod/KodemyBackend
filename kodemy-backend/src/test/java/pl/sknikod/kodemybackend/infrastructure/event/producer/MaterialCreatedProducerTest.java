package pl.sknikod.kodemybackend.infrastructure.event.producer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.cloud.stream.function.StreamBridge;
import pl.sknikod.kodemybackend.SuperclassTest;
import pl.sknikod.kodemybackend.infrastructure.database.Category;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.Section;

import java.time.LocalDateTime;

class MaterialCreatedProducerTest extends SuperclassTest {

    private final StreamBridge streamBridge = Mockito.mock(StreamBridge.class);

    private final MaterialCreatedProducer materialCreatedProducer = new MaterialCreatedProducer(streamBridge);

    private final ArgumentCaptor<Object> messageCaptor = ArgumentCaptor.forClass(Object.class);

    @Test
    void publish_success() {
        var material = new Material();
        material.setId(1L);
        material.setCreatedDate(LocalDateTime.now());
        var category = new Category();
        category.setSection(new Section());
        material.setCategory(category);

        materialCreatedProducer.publish(MaterialCreatedProducer.Message.map(material, "username"));

        Mockito.verify(streamBridge, Mockito.times(1))
                .send(Mockito.any(), messageCaptor.capture());
        Assertions.assertEquals(((MaterialCreatedProducer.Message) messageCaptor.getValue()).getId(), material.getId());
    }
}