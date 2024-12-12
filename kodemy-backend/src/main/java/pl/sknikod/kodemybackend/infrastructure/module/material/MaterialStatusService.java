package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.StatusesToChangeResponse;
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore;
import pl.sknikod.kodemycommons.exception.Validation400Exception;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommons.security.UserPrincipal;
import pl.sknikod.kodemycommons.util.PrincipalUtil;

import static pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialStatusUtil.getAuthorityForStatusChange;
import static pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialStatusUtil.getPossibleStatuses;

@Service
@AllArgsConstructor
public class MaterialStatusService {
    private final MaterialStore materialStore;
    private final PrincipalUtil principalUtil;

    public Material.MaterialStatus update(Long materialId, Material.MaterialStatus newStatus) {
        return materialStore.findById(materialId, true)
                .map(MaterialStore.FindByIdObject::material)
                .filter(material -> {
                    var possibleStatuses = getPossibleStatuses(material.getStatus());
                    var neededAuthority = getAuthorityForStatusChange(material.getStatus(), newStatus);
                    return possibleStatuses.contains(newStatus) && canUserUpdateStatus(neededAuthority, material);
                })
                .flatMap(unused -> materialStore.changeStatus(materialId, newStatus))
                .map(tuple -> tuple._2)
                .toTry(() -> new Validation400Exception("Cannot update status of the material"))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    public StatusesToChangeResponse showStatusesToChange(Long materialId) {
        return materialStore.findById(materialId, true)
                .map(MaterialStore.FindByIdObject::material)
                .map(material -> getPossibleStatuses(material.getStatus()))
                .map(StatusesToChangeResponse::new)
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private boolean canUserUpdateStatus(SimpleGrantedAuthority neededAuthority, Material material) {
        UserPrincipal userPrincipal = principalUtil.getPrincipal().get();
        return userPrincipal.getAuthorities().contains(neededAuthority)
                || isOwnerStatusUpdatePossible(neededAuthority, material, userPrincipal.getId());
    }

    private boolean isOwnerStatusUpdatePossible(SimpleGrantedAuthority neededAuthority, Material material, Long principalId) {
        return neededAuthority == null && material.getUserId().equals(principalId);
    }
}
