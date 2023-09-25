package pl.sknikod.kodemy.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemy.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemy.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemy.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemy.infrastructure.material.rest.*;
import pl.sknikod.kodemy.infrastructure.search.SearchService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;
import pl.sknikod.kodemy.infrastructure.search.rest.SearchFields;
import pl.sknikod.kodemy.infrastructure.user.UserService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final GradeMapper gradeMapper;
    private final GradeRepository gradeRepository;
    private final UserService userService;
    private final MaterialCreateUseCase materialCreateUseCase;
    private final MaterialUpdateUseCase materialUpdateUseCase;
    private final SearchService searchService;
    private final MaterialMapper materialMapper;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        return materialCreateUseCase.execute(body);
    }

    public MaterialUpdateResponse update(Long materialId, MaterialUpdateRequest body) {
        return materialUpdateUseCase.execute(materialId, body);
    }

    public SearchService.ReindexResult reindexMaterial(Date from, Date to) {
        return searchService.reindexMaterialsAsync(from, to);
    }

    public void addGrade(Long materialId, MaterialAddGradeRequest body) {
        Material material = materialRepository.findById(materialId).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)
        );
        Option.of(body)
                .map(gradeMapper::map)
                .map(grade -> {
                    grade.setUser(userService.getContextUser());
                    grade.setMaterial(material);
                    return grade;
                })
                .map(gradeRepository::save)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    public Page<SingleGradeResponse> showGrades(int size, int page, String sort, Sort.Direction sortDirection, Long materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId));
        List<SingleGradeResponse> singleGradeList = new ArrayList<>(gradeMapper.map(material.getGrades()));
        return new PageImpl<>(singleGradeList, PageRequest.of(page, size, sortDirection, sort), singleGradeList.size());
    }


    public Page<MaterialSearchObject> search(SearchFields searchFields, int size, int page, String sort, Sort.Direction sortDirection) {
        return searchService.searchMaterials(searchFields, PageRequest.of(page, size, sortDirection, sort));
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
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }
}