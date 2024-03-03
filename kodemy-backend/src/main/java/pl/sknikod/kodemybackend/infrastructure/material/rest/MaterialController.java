package pl.sknikod.kodemybackend.infrastructure.material.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.*;

import java.net.URI;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final MaterialCreateUseCase materialCreateUseCase;
    private final MaterialUpdateUseCase materialUpdateUseCase;
    private final MaterialGetUseCase materialGetUseCase;
    private final MaterialAdminGetUseCase materialManageGetUseCase;
    private final MaterialOSearchUseCase materialOSearchUseCase;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialCreateUseCase.MaterialCreateResponse> create(MaterialCreateUseCase.MaterialCreateRequest body) {
        var materialResponse = materialCreateUseCase.create(body);
        return ResponseEntity
                .created(URI.create("/api/materials/" + materialResponse.getId()))
                .body(materialResponse);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialUpdateUseCase.MaterialUpdateResponse> update(Long materialId, MaterialUpdateUseCase.MaterialUpdateRequest body) {
        var materialResponse = materialUpdateUseCase.update(materialId, body);
        return ResponseEntity
                .ok().body(materialResponse);
    }

    @Override
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_INDEX')")
    public ResponseEntity<MaterialOSearchUseCase.ReindexResult> reindex(Instant from, Instant to) {
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(materialOSearchUseCase.reindex(from, to));
    }

    @Override
    @PreAuthorize("hasAuthority('CAN_APPROVED_MATERIAL')")
    public ResponseEntity<SingleMaterialResponse> changeStatus(Long materialId, Material.MaterialStatus status) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialUpdateUseCase.changeStatus(materialId, status));
    }

    @Override
    public ResponseEntity<SingleMaterialResponse> showDetails(Long materialId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGetUseCase.showDetails(materialId));
    }

    @Override
    //@PreAuthorize("hasAuthority('CAN_VIEW_ALL_MATERIALS')")
    public ResponseEntity<Page<MaterialPageable>> manage(
            int size,
            int page,
            String sort,
            Sort.Direction sortDirection,
            SearchFields searchFields
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialManageGetUseCase.manage(
                        Objects.isNull(searchFields) ? new SearchFields() : searchFields,
                        PageRequest.of(page, size, sortDirection, sort)
                ));
    }
}