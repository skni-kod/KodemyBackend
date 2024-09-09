package pl.sknikod.kodemybackend.infrastructure.module.material.get;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.sknikod.kodemybackend.infrastructure.common.lan.LanNetworkHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.GradeRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.database.handler.MaterialRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

@Slf4j
@RequiredArgsConstructor
public class MaterialGetByIdUseCase {
    private final GradeRepositoryHandler gradeRepositoryHandler;
    private final MaterialRepositoryHandler materialRepositoryHandler;
    private final LanNetworkHandler lanNetworkHandler;

    public SingleMaterialResponse showDetails(Long materialId) {
        return materialRepositoryHandler.findById(materialId)
                .mapTry(materialEnt -> SingleMaterialResponse.map(
                        materialEnt,
                        gradeRepositoryHandler.findAvgGradeByMaterial(materialId)
                                .getOrElseThrow(ExceptionUtil::throwIfFailure),
                        gradeRepositoryHandler.getGradeStats(materialId)
                                .getOrElseThrow(ExceptionUtil::throwIfFailure),
                        lanNetworkHandler.getUser(materialEnt.getUserId())
                                .map(LanNetworkHandler.SimpleUserResponse::getUsername)
                                .getOrElseThrow(ExceptionUtil::throwIfFailure)
                ))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
