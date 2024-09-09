package pl.sknikod.kodemybackend.infrastructure.module.material.update;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.handler.MaterialRepositoryHandler;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.exception.Validation400Exception;
import pl.sknikod.kodemycommon.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommon.security.AuthFacade;
import pl.sknikod.kodemycommon.security.UserPrincipal;

import static pl.sknikod.kodemybackend.infrastructure.common.model.MaterialStatusUtil.getAuthorityForStatusChange;
import static pl.sknikod.kodemybackend.infrastructure.common.model.MaterialStatusUtil.getPossibleStatuses;

@AllArgsConstructor
public class MaterialStatusUseCase {
    private final MaterialRepositoryHandler materialRepositoryHandler;

    public Material.MaterialStatus update(Long materialId, Material.MaterialStatus newStatus) {
        var userPrincipal = AuthFacade.getCurrentUserPrincipal()
                .orElseThrow(InternalError500Exception::new);
        return materialRepositoryHandler.findById(materialId)
                .filter(material -> {
                    var possibleStatuses = getPossibleStatuses(material.getStatus());
                    var neededAuthority = getAuthorityForStatusChange(material.getStatus(), newStatus);
                    return possibleStatuses.contains(newStatus) && canUserUpdateStatus(userPrincipal, neededAuthority, material);
                })
                .peek(m -> m.setStatus(newStatus))
                .flatMap(materialRepositoryHandler::save)
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
