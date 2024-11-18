package pl.sknikod.kodemybackend.infrastructure.module.material_by_user;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.FilterSearchParams;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore;
import pl.sknikod.kodemybackend.infrastructure.store.UserStore;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemycommons.security.AuthFacade;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaterialGetByUserService {
    private final MaterialStore materialStore;
    private static final SimpleGrantedAuthority CAN_VIEW_ALL_MATERIALS =
            new SimpleGrantedAuthority("CAN_VIEW_ALL_MATERIALS");

    public Page<MaterialPageable> manage(@NotNull FilterSearchParams filterSearchParams, PageRequest pageRequest) {
        return materialStore.findAll(filterSearchParams, filterSearchParams.getStatuses(), filterSearchParams.getUserId(), pageRequest)
                .mapTry(tuple -> mapToMaterialPageable(tuple._1, tuple._2))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private Page<MaterialPageable> mapToMaterialPageable(Page<MaterialStore.FindAllPageObject> materials, UserStore.User user) {
        return materials.map(material1 -> MaterialPageable.map(
                material1.getMaterial(), material1.getAvgGrade(), user.getUsername()
        ));
    }

    public Page<MaterialPageable> getPersonalMaterials(Long userId, FilterSearchParams filterSearchParams, PageRequest pageRequest) {
        var statuses = (userCannotViewNotApprovedMaterials(userId))
                ? List.of(Material.MaterialStatus.APPROVED) : filterSearchParams.getStatuses();
        return materialStore.findAll(filterSearchParams, statuses, userId, pageRequest)
                .mapTry(tuple -> mapToMaterialPageable(tuple._1, tuple._2))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private static boolean userCannotViewNotApprovedMaterials(Long userId) {
        return AuthFacade.getCurrentUserPrincipal()
                .map(principal -> !principal.getAuthorities().contains(CAN_VIEW_ALL_MATERIALS) &&
                        !userId.equals(principal.getId()))
                .orElse(false);
    }
}
