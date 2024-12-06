package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.StatusesToChangeResponse;
import pl.sknikod.kodemybackend.infrastructure.rest.MaterialControllerDefinition;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final MaterialCreateService materialCreateService;
    private final MaterialUpdateService materialUpdateService;
    private final MaterialGetByIdService materialGetByIdService;
    private final MaterialIndexService materialIndexService;
    private final MaterialStatusService materialStatusService;
    private final MaterialManageService materialManageService;

    @Override
    public ResponseEntity<MaterialCreateService.MaterialCreateResponse> create(MaterialCreateService.MaterialCreateRequest body) {
        var materialResponse = materialCreateService.create(body);
        return ResponseEntity
                .created(URI.create("/api/materials/" + materialResponse.id()))
                .body(materialResponse);
    }

    @Override
    public ResponseEntity<MaterialUpdateService.MaterialUpdateResponse> update(
            Long materialId, MaterialUpdateService.MaterialUpdateRequest body
    ) {
        var materialResponse = materialUpdateService.update(materialId, body);
        return ResponseEntity.ok().body(materialResponse);
    }

    @Override
    public ResponseEntity<Material.MaterialStatus> updateStatus(Long materialId, Material.MaterialStatus newStatus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialStatusService.update(materialId, newStatus));
    }

    @Override
    public ResponseEntity<Void> reindex(Instant from, Instant to) {
        materialIndexService.reindex(from, to);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Override
    public ResponseEntity<SingleMaterialResponse> showDetails(Long materialId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGetByIdService.showDetails(materialId));
    }

    @Override
    public ResponseEntity<StatusesToChangeResponse> showStatusesToChange(Long materialId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialStatusService.showStatusesToChange(materialId));
    }

    @Override
    public ResponseEntity<Page<MaterialPageable>> manage(int size, int page, MaterialSortField sortField, Sort.Direction sortDirection, MaterialFilterSearchParams filterSearchParams) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialManageService.manage(
                        Objects.requireNonNullElse(filterSearchParams, new MaterialFilterSearchParams()),
                        PageRequest.of(page, size, sortDirection, sortField.getField())
                ));
    }
}