package pl.sknikod.kodemy.infrastructure.material;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.configuration.RabbitConfig;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

@Component
@AllArgsConstructor
public class MaterialAsyncService {
    private final RabbitTemplate rabbitTemplate;

    public void sendToIndex(Material material) {
        var materialOpenSearch = MaterialSearchObject.builder()
                .id(material.getId())
                .title(material.getTitle())
                .description(material.getDescription())
                .link(material.getLink())
                .status(material.getStatus())
                .isActive(material.isActive())
                .user(material.getUser().getUsername())
                .createdDate(material.getCreatedDate())
                .categoryId(material.getCategory().getId())
                .build();

        rabbitTemplate.convertAndSend(
                MaterialConfig.NAME_CREATED + RabbitConfig.EXCHANGE_SUFFIX,
                "",
                materialOpenSearch
        );
    }
}
