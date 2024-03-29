package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.ContextUtil;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SearchFields;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleMaterialResponse;

import java.util.ArrayList;
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
    private final MaterialPageableMapper materialPageableMapper;
    private final ContextUtil contextUtil;

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

    public Page<MaterialPageable> getPersonalMaterials(Long authorId, SearchFields searchFields, PageRequest pageRequest) {
        var principal = contextUtil.getCurrentUserPrincipal();
        var statuses = searchFields.getStatuses();
        if (!principal.getAuthorities().contains(new SimpleGrantedAuthority("CAN_VIEW_ALL_MATERIALS")) && !authorId.equals(principal.getId())) {
            statuses = new ArrayList<>(List.of(Material.MaterialStatus.APPROVED));
        }

        var materials = materialRepository.searchMaterialsWithAvgGrades(
                        searchFields.getId(),
                        searchFields.getPhrase(),
                        statuses,
                        searchFields.getCreatedBy(),
                        searchFields.getSectionId(),
                        searchFields.getCategoryIds(),
                        searchFields.getTagIds(),
                        authorId,
                        searchFields.getCreatedDateFrom(),
                        searchFields.getCreatedDateTo(),
                        pageRequest
                ).stream()
                .map(material -> materialPageableMapper.map((Material) material[0], (Double) material[1]))
                .toList();

        return new PageImpl<>(
                materials,
                pageRequest,
                materials.size()
        );
    }



    private List<Long> fetchGradeStats(Long materialId) {
        return Stream.iterate(1.0, i -> i <= 5.0, i -> i + 1.0)
                .map(i -> gradeRepository.countAllByMaterialIdAndValue(materialId, i))
                .collect(Collectors.toList());
    }
}
