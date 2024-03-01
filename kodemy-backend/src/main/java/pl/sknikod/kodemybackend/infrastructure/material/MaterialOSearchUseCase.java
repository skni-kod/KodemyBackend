package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.RabbitConfig;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialOSearchUseCase {
    private final GradeRepository gradeRepository;
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig.QueueProperties queueProperties;
    private final MaterialRabbitMapper rabbitMapper;
    private final AtomicInteger reindexTasksCounter = new AtomicInteger(0);
    private final AtomicInteger reindexObjectsCounter = new AtomicInteger(0);
    private final AtomicInteger reindexMaterials = new AtomicInteger(0);
    private final Integer MAX_PAGE_SIZE_FOR_INDEX = 2000;
    private final Integer MAX_CONCURRENT_TASKS = 100;

    public ReindexResult reindex(Instant from, Instant to) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        reindexTasksCounter.set(0);
        reindexObjectsCounter.set(0);
        reindexMaterials.set(0);
        var fromDate = Date.from(from);
        var toDate = Date.from(to);
        var mUpdatedQueueName = queueProperties.get("m-updated").getName();

        Pageable pageable = PageRequest.of(0, MAX_PAGE_SIZE_FOR_INDEX);
        Page<Material> materialPage;
        do {
            materialPage = materialRepository.findMaterialsInDateRangeWithPage(fromDate, toDate, pageable);
            var materialsToIndex = materialPage.getContent();
            var gradesMap = gradeRepository.findAverageGradeByMaterialsIds(
                            materialsToIndex.stream().map(Material::getId).toList()
                    )
                    .stream()
                    .collect(Collectors.toMap(result -> (Long) result[0], result -> (Double) result[1]));
            reindexObjectsCounter.addAndGet(materialsToIndex.size());
            reindexMaterials.addAndGet(materialsToIndex.size());
            executorService.submit(() -> {
                reindexTasksCounter.incrementAndGet();
                materialsToIndex
                        .forEach(material -> {
                            rabbitTemplate.convertAndSend(
                                    mUpdatedQueueName,
                                    "",
                                    rabbitMapper.map(
                                            material, gradesMap.getOrDefault(material.getId(), 0.00)
                                    )
                            );
                            reindexObjectsCounter.decrementAndGet();
                        });
                reindexTasksCounter.decrementAndGet();
            });
            while (reindexTasksCounter.get() > MAX_CONCURRENT_TASKS) ; //wait
            pageable = pageable.next();
        } while (materialPage.hasNext());

        while (reindexObjectsCounter.get() > 0 || reindexTasksCounter.get() > 0) ; // wait
        executorService.shutdown();
        return new ReindexResult(reindexMaterials.get());
    }

    @Value
    public static class ReindexResult {
        long value;
    }

    public void index(Material material) {
        rabbitTemplate.convertAndSend(
                queueProperties.get("m-created").getName(),
                "",
                rabbitMapper.map(
                        material, gradeRepository.findAvgGradeByMaterialId(material.getId())
                )
        );
    }
}
