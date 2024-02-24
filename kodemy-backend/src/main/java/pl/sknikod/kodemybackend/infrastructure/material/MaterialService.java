package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.configuration.RabbitConfig;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.EntityDao;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MaterialService {
    private final GradeMapper gradeMapper;
    private final GradeRepository gradeRepository;
    private final MaterialCreateUseCase materialCreateUseCase;
    private final MaterialUpdateUseCase materialUpdateUseCase;
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;
    private final EntityDao entityDao;
    private final RabbitTemplate rabbitTemplate;
    private final RabbitConfig.QueueProperties queueProperties;
    private final MaterialRabbitMapper rabbitMapper;
    private final AtomicInteger reindexTasksCounter = new AtomicInteger(0);
    private final AtomicInteger reindexObjectsCounter = new AtomicInteger(0);
    private final AtomicInteger reindexMaterials = new AtomicInteger(0);
    private final Integer MAX_PAGE_SIZE_FOR_INDEX = 2000;
    private final Integer MAX_CONCURRENT_TASKS = 100;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        return materialCreateUseCase.execute(body);
    }

    public MaterialUpdateResponse update(Long materialId, MaterialUpdateRequest body) {
        return materialUpdateUseCase.execute(materialId, body);
    }

    public void addGrade(Long materialId, MaterialAddGradeRequest body) {
        Option.of(body)
                .map(request -> gradeMapper.map(
                        request,
                        entityDao.findMaterialById(materialId),
                        (SecurityConfig.UserPrincipal) SecurityContextHolder.getContext()
                                .getAuthentication().getPrincipal()
                ))
                .map(gradeRepository::save)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    public Page<SingleGradeResponse> showGrades(PageRequest pageRequest, GradeMaterialSearchFields searchFields) {
        Date minDate = Objects.nonNull(searchFields.getCreatedDateFrom())
                ? searchFields.getCreatedDateFrom() : GradeRepository.DATE_MIN;
        Date maxDate = Objects.nonNull(searchFields.getCreatedDateTo())
                ? searchFields.getCreatedDateTo() : GradeRepository.DATE_MAX;
        Page<Grade> gradesPage = gradeRepository.findGradesByMaterialInDateRange(
                searchFields.getId(),
                minDate, maxDate,
                PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize())
        );
        List<SingleGradeResponse> singleGradeResponses = gradesPage.getContent()
                .stream()
                .map(gradeMapper::map)
                .toList();
        return new PageImpl<>(singleGradeResponses, gradesPage.getPageable(), gradesPage.getTotalElements());
    }

    public SingleMaterialResponse showDetails(Long materialId) {
        return Option.of(entityDao.findMaterialById(materialId))
                .map(material -> materialMapper.map(
                        material,
                        gradeRepository.findAverageGradeByMaterialId(materialId),
                        fetchGradeStats(materialId))
                )
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private List<Long> fetchGradeStats(Long materialId) {
        return Stream.iterate(1.0, i -> i <= 5.0, i -> i + 1.0)
                .map(i -> gradeRepository.countAllByMaterialIdAndValue(materialId, i))
                .collect(Collectors.toList());
    }

    public SingleMaterialResponse changeStatus(Long materialId, Material.MaterialStatus status) {
        return Option.of(entityDao.findMaterialById(materialId))
                .peek(material -> material.setStatus(status))
                .map(materialRepository::save)
                .map(materialMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    public ReindexResult reindexMaterial(Date from, Date to) {
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        reindexTasksCounter.set(0);
        reindexObjectsCounter.set(0);
        reindexMaterials.set(0);
        Pageable pageable = PageRequest.of(0, MAX_PAGE_SIZE_FOR_INDEX);
        Page<Material> materialPage;
        do {
            materialPage = materialRepository.findMaterialsInDateRangeWithPage(from, to, pageable);
            var materialsToIndex = materialPage.getContent();
            var gradesMap = gradeRepository.findAverageGradeByMaterialsIds(
                            materialsToIndex.stream().map(Material::getId).toList()
                    )
                    .stream()
                    .collect(Collectors.toMap(
                            result -> (Long) result[0],
                            result -> (Double) result[1]
                    ));
            reindexObjectsCounter.addAndGet(materialsToIndex.size());
            reindexMaterials.addAndGet(materialsToIndex.size());
            executorService.submit(() -> {
                reindexTasksCounter.incrementAndGet();
                materialsToIndex
                        .forEach(material -> {
                            var searchObj = materialMapper.map(
                                    material,
                                    gradesMap.getOrDefault(material.getId(), 0.00)
                            );
                            rabbitTemplate.convertAndSend(
                                    queueProperties.get("m-updated").getName(),
                                    "",
                                    rabbitMapper.map(
                                            material,
                                            gradeRepository.findAverageGradeByMaterialId(material.getId())
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

    public Page<SingleMaterialResponse> searchMaterials(SearchFields searchFields, PageRequest pageRequest) {
        return new PageImpl<>(
                materialRepository
                        .searchMaterials(searchFields, pageRequest)
                        .stream()
                        .map(materialMapper::map)
                        .toList(),
                pageRequest,
                materialRepository.count()
        );
    }

    @Value
    public static class ReindexResult {
        long value;
    }

}