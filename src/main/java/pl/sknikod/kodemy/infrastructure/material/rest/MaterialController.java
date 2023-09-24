package pl.sknikod.kodemy.infrastructure.material.rest;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemy.infrastructure.material.MaterialService;
import pl.sknikod.kodemy.infrastructure.search.SearchService;
import pl.sknikod.kodemy.infrastructure.search.rest.MaterialSearchObject;
import pl.sknikod.kodemy.infrastructure.search.rest.SearchFields;

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
    public ResponseEntity<SearchService.ReindexResult> reindex(Date from, Date to) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(materialService.reindexMaterial(from, to));
    }

    @Override
    public ResponseEntity<Page<MaterialSearchObject>> search(int size, int page, String sort, Sort.Direction sortDirection, SearchFields searchFields) {
        return ResponseEntity.status(HttpStatus.OK).body(materialService.search(searchFields, size, page, sort, sortDirection));
    }

}