package pl.sknikod.kodemy.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemy.exception.structure.NotFoundException;
import pl.sknikod.kodemy.exception.structure.ServerProcessingException;
import pl.sknikod.kodemy.infrastructure.common.entity.Material;
import pl.sknikod.kodemy.infrastructure.common.mapper.GradeMapper;
import pl.sknikod.kodemy.infrastructure.common.repository.GradeRepository;
import pl.sknikod.kodemy.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialAddGradeRequest;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialCreateRequest;
import pl.sknikod.kodemy.infrastructure.material.rest.MaterialCreateResponse;
import pl.sknikod.kodemy.infrastructure.material.rest.SingleGradeResponse;
import pl.sknikod.kodemy.infrastructure.user.UserService;

import java.util.Set;

@Service
@AllArgsConstructor
public class MaterialService {
    private final MaterialRepository materialRepository;
    private final GradeMapper gradeMapper;
    private final GradeRepository gradeRepository;
    private final UserService userService;
    private final MaterialCreateUseCase materialCreateUseCase;

    public MaterialCreateResponse create(MaterialCreateRequest body) {
        return materialCreateUseCase.execute(body);
    }

    public void addGrade(MaterialAddGradeRequest body, Long materialId) {
        Material material = materialRepository.findById(materialId).orElseThrow(() ->
                new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId)
        );
        Option.of(body)
                .map(gradeMapper::map)
                .peek(grade -> {
                    grade.setUser(userService.getContextUser());
                    grade.setMaterial(material);
                })
                .map(gradeRepository::save)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }

    public Set<SingleGradeResponse> showGrades(Long materialId) {
        return Option.of(materialRepository.findById(materialId).orElseThrow(() ->
                        new NotFoundException(NotFoundException.Format.ENTITY_ID, Material.class, materialId))
                )
                .map(Material::getGrades)
                .map(gradeMapper::map)
                .getOrElseThrow(() -> new ServerProcessingException(ServerProcessingException.Format.PROCESS_FAILED, Material.class));
    }
}