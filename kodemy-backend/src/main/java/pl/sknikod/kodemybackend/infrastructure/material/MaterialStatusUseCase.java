package pl.sknikod.kodemybackend.infrastructure.material;

import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.exception.structure.ValidationException;
import pl.sknikod.kodemybackend.util.ContextUtil;
import pl.sknikod.kodemybackend.infrastructure.common.MaterialStatusUtil;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;

import java.util.List;

@Component
@AllArgsConstructor
public class MaterialStatusUseCase {
    private final MaterialRepository materialRepository;

    public Material.MaterialStatus update(Long materialId, Material.MaterialStatus newStatus) throws ValidationException {
        Material material = materialRepository.findMaterialById(materialId);
        var userPrincipal = Option.ofOptional(ContextUtil.getCurrentUserPrincipal())
                .getOrElseThrow(() -> new ValidationException("User not authorized"));

        List<Material.MaterialStatus> possibleStatuses = MaterialStatusUtil.getPossibleStatuses(material.getStatus());
        var neededAuthority = MaterialStatusUtil.getAuthorityForStatusChange(material.getStatus(), newStatus);

        if (possibleStatuses.contains(newStatus) && canUserUpdateStatus(userPrincipal, neededAuthority, material)) {
            return updateStatus(material, newStatus);
        }
        throw new ValidationException("Cannot update status of the material");
    }

    private boolean canUserUpdateStatus(SecurityConfig.UserPrincipal userPrincipal, SimpleGrantedAuthority neededAuthority, Material material) {
        return userPrincipal.getAuthorities().contains(neededAuthority)
                || isOwnerStatusUpdatePossible(neededAuthority, material, userPrincipal.getId());
    }

    private boolean isOwnerStatusUpdatePossible(SimpleGrantedAuthority neededAuthority, Material material, Long principalId) {
        return neededAuthority == null && material.getAuthor().getId().equals(principalId);
    }

    private Material.MaterialStatus updateStatus(Material material, Material.MaterialStatus newStatus) {
        return Option.of(material)
                .peek(m -> m.setStatus(newStatus))
                .map(materialRepository::save)
                .map(Material::getStatus)
                .getOrElseThrow(
                        () -> new ValidationException("Cannot update status of the material")
                );
    }
}
