package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse;
import pl.sknikod.kodemybackend.infrastructure.store.GradeStore;
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialGetByIdService {
    private final MaterialStore materialStore;

    public SingleMaterialResponse showDetails(Long materialId) {
        return materialStore.findById(materialId)
                .mapTry(findByIdObject -> SingleMaterialResponse.map(
                        findByIdObject.material(), findByIdObject.username(),
                        findByIdObject.avgGrade(), findByIdObject.gradeStats()
                ))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
