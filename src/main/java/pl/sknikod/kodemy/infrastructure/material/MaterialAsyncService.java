package pl.sknikod.kodemy.infrastructure.material;

import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemy.configuration.RabbitConfig;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemy.infrastructure.search.MaterialSearchMapper;

@Component
@AllArgsConstructor
public class MaterialAsyncService {
    private final RabbitTemplate rabbitTemplate;
    private final MaterialSearchMapper materialSearchMapper;
    private final GradeRepository gradeRepository;

    public void sendToIndex(Material material) {
        rabbitTemplate.convertAndSend(
                MaterialConfig.NAME_CREATED + RabbitConfig.EXCHANGE_SUFFIX,
                "",
                materialSearchMapper.map(
                        material,
                        gradeRepository.findAverageGradeByMaterialId(material.getId())
                )
        );
    }

    public void sendToReindex(Material material) {
        rabbitTemplate.convertAndSend(
                MaterialConfig.NAME_UPDATED + RabbitConfig.EXCHANGE_SUFFIX,
                "",
                materialSearchMapper.map(
                        material,
                        gradeRepository.findAverageGradeByMaterialId(material.getId())
                )
        );
    }
}
