package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.dao.MaterialDao;
import pl.sknikod.kodemybackend.infrastructure.module.material.producer.MaterialStatusUpdatedProducer;
import pl.sknikod.kodemybackend.infrastructure.module.material.producer.MaterialUpdatedProducer;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;
import pl.sknikod.kodemycommons.exception.Validation400Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommons.security.AuthFacade;
import pl.sknikod.kodemycommons.security.UserPrincipal;

import static pl.sknikod.kodemybackend.infrastructure.common.model.MaterialStatusUtil.getAuthorityForStatusChange;
import static pl.sknikod.kodemybackend.infrastructure.common.model.MaterialStatusUtil.getPossibleStatuses;

@AllArgsConstructor
public class MaterialStatusService {
    private final MaterialDao materialDao;
    private final MaterialStatusUpdatedProducer materialStatusUpdatedProducer;

    public Material.MaterialStatus update(Long materialId, Material.MaterialStatus newStatus) {
        var userPrincipal = AuthFacade.getCurrentUserPrincipal()
                .orElseThrow(InternalError500Exception::new);
        return materialDao.findById(materialId)
                .filter(material -> {
                    var possibleStatuses = getPossibleStatuses(material.getStatus());
                    var neededAuthority = getAuthorityForStatusChange(material.getStatus(), newStatus);
                    return possibleStatuses.contains(newStatus) && canUserUpdateStatus(userPrincipal, neededAuthority, material);
                })
                .peek(m -> m.setStatus(newStatus))
                .flatMap(materialDao::save)
                .peek(material -> materialStatusUpdatedProducer.publish(
                        new MaterialStatusUpdatedProducer.Message(materialId, material.getStatus())
                ))
                .map(Material::getStatus)
                .toTry(() -> new Validation400Exception("Cannot update status of the material"))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private boolean canUserUpdateStatus(UserPrincipal userPrincipal, SimpleGrantedAuthority neededAuthority, Material material) {
        return userPrincipal.getAuthorities().contains(neededAuthority)
                || isOwnerStatusUpdatePossible(neededAuthority, material, userPrincipal.getId());
    }

    private boolean isOwnerStatusUpdatePossible(SimpleGrantedAuthority neededAuthority, Material material, Long principalId) {
        return neededAuthority == null && material.getUserId().equals(principalId);
    }
}
