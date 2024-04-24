package pl.sknikod.kodemybackend.infrastructure.material.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.*;
import pl.sknikod.kodemybackend.util.ContextUtil;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final MaterialCreateUseCase materialCreateUseCase;
    private final MaterialUpdateUseCase materialUpdateUseCase;
    private final MaterialGetUseCase materialGetUseCase;
    private final MaterialAdminGetUseCase materialManageGetUseCase;
    private final MaterialOSearchUseCase materialOSearchUseCase;
    private final MaterialStatusUseCase materialStatusUseCase;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialCreateUseCase.MaterialCreateResponse> create(MaterialCreateUseCase.MaterialCreateRequest body) {
        var materialResponse = materialCreateUseCase.create(body);
        return ResponseEntity
                .created(URI.create("/api/materials/" + materialResponse.id()))
                .body(materialResponse);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialUpdateUseCase.MaterialUpdateResponse> update(
            Long materialId, MaterialUpdateUseCase.MaterialUpdateRequest body
    ) {
        var materialResponse = materialUpdateUseCase.update(materialId, body);
        return ResponseEntity
                .ok().body(materialResponse);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Material.MaterialStatus> updateStatus(Long materialId, Material.MaterialStatus newStatus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialStatusUseCase.update(materialId, newStatus));
    }

    @Override
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_INDEX')")
    public ResponseEntity<MaterialOSearchUseCase.ReindexResult> reindex(Instant from, Instant to) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(materialOSearchUseCase.reindex(from, to));
    }

    @Override
    public ResponseEntity<SingleMaterialResponse> showDetails(Long materialId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGetUseCase.showDetails(materialId));
    }

    @Override
    public ResponseEntity<Page<MaterialPageable>> materials(
            Long authorId,
            int size,
            int page,
            MaterialSortField sortField,
            Sort.Direction sortDirection,
            SearchFields searchFields
    ) {
        var principal = ContextUtil.getCurrentUserPrincipal();
        return ResponseEntity.status(HttpStatus.OK)
                .body((authorId != null) ?
                        materialGetUseCase.getPersonalMaterials(
                                authorId,
                                Objects.isNull(searchFields) ? new SearchFields() : searchFields,
                                PageRequest.of(page, size, sortDirection, sortField.toString()),
                                principal) :
                        materialManageGetUseCase.manage(
                                Objects.isNull(searchFields) ? new SearchFields() : searchFields,
                                PageRequest.of(page, size, sortDirection, sortField.toString()),
                                principal)
                );
    }
}