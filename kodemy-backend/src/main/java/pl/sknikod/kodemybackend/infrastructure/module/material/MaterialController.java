package pl.sknikod.kodemybackend.infrastructure.module.material;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.database.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.module.material.add.MaterialCreateUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.get.MaterialGetUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.get.MaterialGetByIdUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.index.MaterialOSearchUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialSortField;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SearchFields;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SingleMaterialResponse;
import pl.sknikod.kodemybackend.infrastructure.module.material.update.MaterialStatusUseCase;
import pl.sknikod.kodemybackend.infrastructure.module.material.update.MaterialUpdateUseCase;
import pl.sknikod.kodemybackend.infrastructure.rest.MaterialControllerDefinition;

import java.net.URI;
import java.time.Instant;
import java.util.Objects;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final MaterialCreateUseCase materialCreateUseCase;
    private final MaterialUpdateUseCase materialUpdateUseCase;
    private final MaterialGetByIdUseCase materialGetByIdUseCase;
    private final MaterialGetUseCase materialGetUseCase;
    private final MaterialOSearchUseCase materialOSearchUseCase;
    private final MaterialStatusUseCase materialStatusUseCase;

    @Override
    public ResponseEntity<MaterialCreateUseCase.MaterialCreateResponse> create(MaterialCreateUseCase.MaterialCreateRequest body) {
        var materialResponse = materialCreateUseCase.create(body);
        return ResponseEntity
                .created(URI.create("/api/materials/" + materialResponse.id()))
                .body(materialResponse);
    }

    @Override
    public ResponseEntity<MaterialUpdateUseCase.MaterialUpdateResponse> update(
            Long materialId, MaterialUpdateUseCase.MaterialUpdateRequest body
    ) {
        var materialResponse = materialUpdateUseCase.update(materialId, body);
        return ResponseEntity
                .ok().body(materialResponse);
    }

    @Override
    public ResponseEntity<Material.MaterialStatus> updateStatus(Long materialId, Material.MaterialStatus newStatus) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialStatusUseCase.update(materialId, newStatus));
    }

    @Override
    public ResponseEntity<Void> reindex(Instant from, Instant to) {
        materialOSearchUseCase.reindex(from, to);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Override
    public ResponseEntity<SingleMaterialResponse> showDetails(Long materialId) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGetByIdUseCase.showDetails(materialId));
    }

    @Override
    public ResponseEntity<Page<MaterialPageable>> materialsManage(
            int size, int page,
            MaterialSortField sortField, Sort.Direction sortDirection,
            SearchFields searchFields
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGetUseCase.manage(
                        Objects.requireNonNullElse(searchFields, new SearchFields()),
                        PageRequest.of(page, size, sortDirection, sortField.toString())
                ));
    }

    @Override
    public ResponseEntity<Page<MaterialPageable>> materialsPersonal(
            Long authorId, int size, int page,
            MaterialSortField sortField, Sort.Direction sortDirection,
            SearchFields searchFields
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGetUseCase.getPersonalMaterials(
                        authorId,
                        Objects.requireNonNullElse(searchFields, new SearchFields()),
                        PageRequest.of(page, size, sortDirection, sortField.toString())
                ));
    }
}