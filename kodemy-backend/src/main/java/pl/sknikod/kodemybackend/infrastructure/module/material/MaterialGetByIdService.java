package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pl.sknikod.kodemybackend.infrastructure.common.lan.LanNetworkHandler;
import pl.sknikod.kodemybackend.infrastructure.dao.GradeDao;
import pl.sknikod.kodemybackend.infrastructure.dao.MaterialDao;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;

@Slf4j
@RequiredArgsConstructor
public class MaterialGetByIdService {
    private final GradeDao gradeDao;
    private final MaterialDao materialDao;
    private final LanNetworkHandler lanNetworkHandler;

    public SingleMaterialResponse showDetails(Long materialId) {
        return materialDao.findById(materialId)
                .mapTry(materialEnt -> SingleMaterialResponse.map(
                        materialEnt,
                        gradeDao.findAvgGradeByMaterial(materialId)
                                .getOrElseThrow(ExceptionUtil::throwIfFailure),
                        gradeDao.getGradeStats(materialId)
                                .getOrElseThrow(ExceptionUtil::throwIfFailure),
                        lanNetworkHandler.getUser(materialEnt.getUserId())
                                .map(LanNetworkHandler.SimpleUserResponse::getUsername)
                                .getOrElseThrow(ExceptionUtil::throwIfFailure)
                ))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
