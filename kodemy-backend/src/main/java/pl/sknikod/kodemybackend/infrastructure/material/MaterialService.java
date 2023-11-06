package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.NotFoundException;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Grade;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.entity.UserJsonB;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.*;

import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final GradeMapper gradeMapper;
    private final GradeRepository gradeRepository;
    private final MaterialCreateUseCase materialCreateUseCase;
    private final MaterialUpdateUseCase materialUpdateUseCase;
    private final MaterialMapper materialMapper;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        return materialCreateUseCase.execute(body);
    }

    public MaterialUpdateResponse update(Long materialId, MaterialUpdateRequest body) {
        return materialUpdateUseCase.execute(materialId, body);
    }

    public void addGrade(Long materialId, MaterialAddGradeRequest body) {
        Material material = materialRepository.findById(materialId).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)
        );
        var principal = (SecurityConfig.JwtUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Option.of(body)
                .map(gradeMapper::map)
                .map(grade -> {
                    grade.setAuthor(
                            new UserJsonB(principal.getId(), principal.getUsername())
                    );
                    grade.setMaterial(material);
                    return grade;
                })
                .map(gradeRepository::save)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    public Page<SingleGradeResponse> showGrades(Long materialId, Date from, Date to, int page, int size) {
        Page<Grade> gradesPage = gradeRepository.findGradesByMaterialInDateRangeWithPage(materialId, from, to, PageRequest.of(page, size));
        List<SingleGradeResponse> singleGradeResponses = gradesPage.getContent().stream()
                .map(gradeMapper::map)
                .toList();
        return new PageImpl<>(singleGradeResponses, gradesPage.getPageable(), gradesPage.getTotalElements());
    }

    public SingleMaterialResponse showDetails(Long materialId) {
        return Option.of(materialRepository.findById(materialId).orElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)))
                .peek(System.out::println)
                .map(materialMapper::map)
                .map(materialResponse -> {
                    materialResponse.setAverageGrade(gradeRepository.findAverageGradeByMaterialId(materialId));
                    return materialResponse;
                })
                .map(materialResponse-> setGradeStats(materialId, materialResponse))
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    private SingleMaterialResponse setGradeStats(Long materialId, SingleMaterialResponse materialResponse) {
        var gradeStats = new TreeMap<Double, Long>();
        for(double i=1.0; i<=5.0; i+=0.5){
            Long amount = gradeRepository.countAllByMaterialIdAndValue(materialId, i);
            gradeStats.put(i, amount);
        }
        materialResponse.setGradeStats(gradeStats);
        return materialResponse;
    }
}