package pl.sknikod.kodemy.infrastructure.material;

import lombok.AllArgsConstructor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.configuration.RabbitConfig;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;

@Component
@AllArgsConstructor
public class MaterialAsyncService {
    private final RabbitTemplate rabbitTemplate;
    private final OpenSearchMapper openSearchMapper;

    public void sendToIndex(Material material) {
        rabbitTemplate.convertAndSend(
                MaterialConfig.NAME_CREATED + RabbitConfig.EXCHANGE_SUFFIX,
                "",
                openSearchMapper.map(material)
        );
    }

    public void sendToReindex(Material material) {
        rabbitTemplate.convertAndSend(
                MaterialConfig.NAME_UPDATED + RabbitConfig.EXCHANGE_SUFFIX,
                "",
                openSearchMapper.map(material)
        );
    }

    @Mapper(componentModel = "spring")
    public interface OpenSearchMapper{
        @Mapping(target = "user", source = "user.username")
        @Mapping(target = "categoryId", source = "category.id")
        MaterialSearchObject map(Material material);
    }
}
