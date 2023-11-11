package pl.sknikod.kodemybackend.infrastructure.material.rest;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialService;

import java.net.URI;

@RestController
@AllArgsConstructor
public class MaterialController implements MaterialControllerDefinition {
    private final MaterialService materialService;

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialCreateResponse> create(MaterialCreateRequest body) {
        var materialResponse = materialService.create(body);
        return ResponseEntity
                .created(URI.create("/api/materials/" + materialResponse.getId()))
                .body(materialResponse);
    }

    @Override
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MaterialUpdateResponse> update(Long materialId, MaterialUpdateRequest body) {
        var materialResponse = materialService.update(materialId, body);
        return ResponseEntity
                .ok().body(materialResponse);
    }

    @Override
    public ResponseEntity<SingleMaterialResponse> showDetails(Long materialId) {
        return ResponseEntity.status(HttpStatus.OK).body(materialService.showDetails(materialId));
    }

    @Override
    @PreAuthorize("hasAuthority('CAN_APPROVED_MATERIAL')")
    public ResponseEntity<SingleMaterialResponse> changeStatus(Long materialId, Material.MaterialStatus status) {
        return ResponseEntity.status(HttpStatus.OK).body(materialService.changeStatus(materialId, status));
    }
}