package pl.sknikod.kodemybackend.infrastructure.module.material.get;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sknikod.kodemybackend.exception.ExceptionUtil;
import pl.sknikod.kodemybackend.exception.structure.ServerProcessingException;
import pl.sknikod.kodemybackend.infrastructure.common.lan.LanNetworkHandler;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.database.handler.MaterialRepositoryHandler;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SearchFields;
import pl.sknikod.kodemybackend.util.auth.AuthFacade;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class MaterialGetUseCase {
    private final MaterialRepositoryHandler materialRepositoryHandler;
    private final LanNetworkHandler lanNetworkHandler;
    private static final SimpleGrantedAuthority CAN_VIEW_ALL_MATERIALS =
            new SimpleGrantedAuthority("CAN_VIEW_ALL_MATERIALS");

    public Page<MaterialPageable> manage(@NotNull SearchFields searchFields, PageRequest pageRequest) {
        var material = materialRepositoryHandler.searchMaterialsWithAvgGrades(
                searchFields, searchFields.getStatuses(), searchFields.getUserId(), pageRequest);
        return material
                .flatMap(this::fetchUsers)
                .mapTry(tuple -> toPage(tuple._1, tuple._2))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private Try<Tuple2<Page<Object[]>, Map<Long, String>>> fetchUsers(Page<Object[]> page) {
        var userIds = page.stream()
                .map(material -> (Material) material[0])
                .map(Material::getUserId)
                .collect(Collectors.toSet());
        return lanNetworkHandler.getUsers(userIds)
                .map(ids -> ids.stream().collect(Collectors.toMap(
                        LanNetworkHandler.SimpleUserResponse::getId,
                        LanNetworkHandler.SimpleUserResponse::getUsername
                )))
                .filter(v -> v.size() == userIds.size())
                .toTry(ServerProcessingException::new)
                .map(users -> Tuple.of(page, users));
    }

    private Page<MaterialPageable> toPage(Page<Object[]> page, Map<Long, String> usersMap) {
        var list = page.stream()
                .map(material1 -> MaterialPageable.map((Material) material1[0], (Double) material1[1],
                        usersMap.get(((Material) material1[0]).getUserId())))
                .toList();
        return new PageImpl<>(list, page.getPageable(), page.getTotalElements());
    }

    public Page<MaterialPageable> getPersonalMaterials(Long userId, SearchFields searchFields, PageRequest pageRequest) {
        var statuses = (userCannotViewNotApprovedMaterials(userId))
                ? List.of(Material.MaterialStatus.APPROVED) : searchFields.getStatuses();
        return materialRepositoryHandler.searchMaterialsWithAvgGrades(searchFields, statuses, userId, pageRequest)
                .flatMap(this::fetchUsers)
                .mapTry(tuple -> toPage(tuple._1, tuple._2))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private static boolean userCannotViewNotApprovedMaterials(Long userId) {
        return AuthFacade.getCurrentUserPrincipal()
                .map(principal -> !principal.getAuthorities().contains(CAN_VIEW_ALL_MATERIALS) &&
                        !userId.equals(principal.getId()))
                .orElse(false);

    }
}
