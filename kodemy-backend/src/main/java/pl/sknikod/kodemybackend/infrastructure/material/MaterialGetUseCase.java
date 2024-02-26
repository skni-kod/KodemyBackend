package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleMaterialResponse;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialGetUseCase {
    private final GradeRepository gradeRepository;
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    public SingleMaterialResponse showDetails(Long materialId) {
        return Option.of(materialRepository.findMaterialById(materialId))
                .map(material -> materialMapper.map(
                        material,
                        gradeRepository.findAvgGradeByMaterialId(materialId),
                        fetchGradeStats(materialId))
                )
                .getOrElseThrow(() -> new ServerProcessingException(
                        ServerProcessingException.Format.PROCESS_FAILED, Material.class
                ));
    }

    private List<Long> fetchGradeStats(Long materialId) {
        return Stream.iterate(1.0, i -> i <= 5.0, i -> i + 1.0)
                .map(i -> gradeRepository.countAllByMaterialIdAndValue(materialId, i))
                .collect(Collectors.toList());
    }
}
