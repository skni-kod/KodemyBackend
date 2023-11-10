package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.auth.AuthService;
import pl.sknikod.kodemybackend.infrastructure.common.EntityDao;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class MaterialService {
    private final GradeMapper gradeMapper;
    private final GradeRepository gradeRepository;
    private final MaterialCreateUseCase materialCreateUseCase;
    private final MaterialUpdateUseCase materialUpdateUseCase;
    private final MaterialMapper materialMapper;
    private final EntityDao entityDao;
    private final AuthService authService;

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
                        authService.getPrincipal()
                ))
                .map(gradeRepository::save)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    public Page<SingleGradeResponse> showGrades(Long materialId, Date from, Date to, int page, int size) {
        // TODO search format
        Page<Grade> gradesPage = gradeRepository.findGradesByMaterialInDateRangeWithPage(
                materialId, from, to, PageRequest.of(page, size)
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
}