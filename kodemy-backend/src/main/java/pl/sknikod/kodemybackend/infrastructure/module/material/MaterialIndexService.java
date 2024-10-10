package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.module.material.producer.MaterialUpdatedProducer;

import java.time.Instant;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class MaterialIndexService {
    private static final Integer MAX_PAGE_SIZE_FOR_INDEX = 2000;
    private static final Integer MAX_CONCURRENT_TASKS = 100;
    private final GradeRepository gradeRepository;
    private final MaterialRepository materialRepository;
    private final MaterialUpdatedProducer materialUpdatedProducer;

    public void reindex(Instant from, Instant to) {
        final var executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        final var fromDate = Date.from(from);
        final var toDate = Date.from(to);
        final var pageable = PageRequest.of(0, MAX_PAGE_SIZE_FOR_INDEX);

        var materialPage = materialRepository.findMaterialsInDateRangeWithPage(fromDate, toDate, pageable);
        final var countDownLatch = new CountDownLatch((int) materialPage.getTotalElements());
        final var semaphore = new Semaphore(MAX_CONCURRENT_TASKS);

        while (materialPage.hasNext()) {
            final var materialsToIndex = materialPage.getContent();
            final var materialIds = materialsToIndex.stream().map(Material::getId).toList();
            final var gradesMap = gradeRepository.findAverageGradeByMaterialsIds(materialIds).stream()
                    .collect(Collectors.toMap(key -> (Long) key[0], key -> (Double) key[1]));
            semaphore.acquireUninterruptibly();
            executorService.submit(() -> {
                try {
                    materialsToIndex.forEach(material -> {
                        var message = MaterialUpdatedProducer.Message.map(
                                material, gradesMap.getOrDefault(material.getId(), 0.00));
                        materialUpdatedProducer.publish(message);
                    });
                } finally {
                    semaphore.release();
                    countDownLatch.countDown();
                }
            });
            materialPage = materialRepository.findMaterialsInDateRangeWithPage(fromDate, toDate, pageable.next());
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }
    }
}
