package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.database.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.database.MaterialRepository;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialIndexService {
    private static final Integer MAX_PAGE_SIZE_FOR_INDEX = 2000;
    private static final Integer MAX_CONCURRENT_TASKS = 100;
    private final GradeRepository gradeRepository;
    private final MaterialRepository materialRepository;

    @Async
    public void reindex(Instant from, Instant to) {
        final var executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {

            final var fromDate = LocalDateTime.ofInstant(from, ZoneId.systemDefault().getRules().getOffset(from));
            final var toDate = LocalDateTime.ofInstant(to, ZoneId.systemDefault().getRules().getOffset(to));
            final var pageable = PageRequest.of(0, MAX_PAGE_SIZE_FOR_INDEX);

            var materialPage = materialRepository.findMaterialsInDateRangeWithPage(fromDate, toDate, pageable);
            final var countDownLatch = new CountDownLatch(materialPage.getTotalPages());

            do {
                final var materialsToIndex = materialPage.getContent();
                final var materialIds = materialsToIndex.stream().map(Material::getId).toList();
                final var gradesMap = gradeRepository.findAverageGradeByMaterialsIds(materialIds).stream()
                        .collect(Collectors.toMap(key -> (Long) key[0], key -> (Double) key[1]));
//                final var users = lanNetworkHandler.getUsers(materialsToIndex.stream().map(Material::getUserId))
//                        .getOrElse(Collections.emptyMap());
                executorService.submit(() -> {
                    try {
                        materialsToIndex.forEach(material -> {
                            final var user = /*users.get(material.getUserId())*/(Long) null;
                            /*if (user != null) {
                                var message = MaterialUpdatedProducer.Message.map(
                                        material, gradesMap.getOrDefault(material.getId(), 0.00),
                                        new MaterialUpdatedProducer.Message.Author(material.getUserId(), user)
                                );
                                materialUpdatedProducer.publish(message);
                            }*/
                        });
                    } finally {
                        countDownLatch.countDown();
                    }
                });
                materialPage = materialRepository.findMaterialsInDateRangeWithPage(fromDate, toDate, pageable.next());
            } while (materialPage.hasNext());
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }
    }
}
