package pl.sknikod.kodemybackend.infrastructure.module.material.update;

import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.exception.ExceptionUtil;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.exception.structure.ValidationException;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.handler.MaterialRepositoryHandler;
import pl.sknikod.kodemybackend.util.auth.AuthFacade;
import pl.sknikod.kodemybackend.util.auth.UserPrincipal;

import static pl.sknikod.kodemybackend.infrastructure.common.model.MaterialStatusUtil.getAuthorityForStatusChange;
import static pl.sknikod.kodemybackend.infrastructure.common.model.MaterialStatusUtil.getPossibleStatuses;

@AllArgsConstructor
public class MaterialStatusUseCase {
    private final MaterialRepositoryHandler materialRepositoryHandler;

    public Material.MaterialStatus update(Long materialId, Material.MaterialStatus newStatus) {
        var userPrincipal = AuthFacade.getCurrentUserPrincipal()
                .orElseThrow(ServerProcessingException::new);
        return materialRepositoryHandler.findById(materialId)
                .filter(material -> {
                    var possibleStatuses = getPossibleStatuses(material.getStatus());
                    var neededAuthority = getAuthorityForStatusChange(material.getStatus(), newStatus);
                    return possibleStatuses.contains(newStatus) && canUserUpdateStatus(userPrincipal, neededAuthority, material);
                })
                .peek(m -> m.setStatus(newStatus))
                .flatMap(materialRepositoryHandler::save)
                .map(Material::getStatus)
                .toTry(() -> new ValidationException("Cannot update status of the material"))
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
