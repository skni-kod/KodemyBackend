package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.event.producer.MaterialUpdatedProducer;
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialIndexService {
    private static final Integer MAX_PAGE_SIZE_FOR_INDEX = 2000;
    private static final Integer MAX_CONCURRENT_TASKS = 100;
    private final MaterialStore materialStore;
    private final MaterialUpdatedProducer materialUpdatedProducer;

    @Async
    public void reindex(Instant from, Instant to) {
        final var executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        try {
            final var fromDate = LocalDateTime.ofInstant(from, ZoneId.systemDefault().getRules().getOffset(from));
            final var toDate = LocalDateTime.ofInstant(to, ZoneId.systemDefault().getRules().getOffset(to));
            final var pageable = PageRequest.of(0, MAX_PAGE_SIZE_FOR_INDEX);
            var findTry = materialStore.findAllInDateRange(fromDate, toDate, pageable);
            if (findTry.isFailure()) {
                return;
            }
            final var countDownLatch = new CountDownLatch(findTry.get().getTotalPages());
            while (findTry.isSuccess()) {
                executorService.submit(reindexTask(findTry.get(), countDownLatch));
                if (!findTry.get().hasNext()) {
                    break;
                }
                findTry = materialStore.findAllInDateRange(fromDate, toDate, pageable.next());
            }
            countDownLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            executorService.shutdown();
        }
    }

    private Runnable reindexTask(Page<MaterialStore.FindAllPageWithUserObject> page, CountDownLatch countDownLatch) {
        return () -> {
            try {
                page.getContent().forEach(findAllObject -> {
                    if (findAllObject.getUsername() != null) {
                        materialUpdatedProducer.publish(MaterialUpdatedProducer.Message.map(
                                findAllObject.getMaterial(), findAllObject.getAvgGrade(), findAllObject.getUsername()
                        ));
                    }
                });
            } finally {
                countDownLatch.countDown();
            }
        };
    }
}
