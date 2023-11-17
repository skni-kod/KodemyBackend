package pl.sknikod.kodemybackend.infrastructure.material.rest;

import com.sun.xml.bind.v2.runtime.unmarshaller.XsiNilLoader;
import lombok.AllArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.material.MaterialService;

import java.net.URI;
import java.util.Date;

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
    @PreAuthorize("isAuthenticated() and hasAuthority('CAN_INDEX')")
    public ResponseEntity<MaterialService.ReindexResult> reindex(Date from, Date to) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(materialService.reindexMaterial(from, to));
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