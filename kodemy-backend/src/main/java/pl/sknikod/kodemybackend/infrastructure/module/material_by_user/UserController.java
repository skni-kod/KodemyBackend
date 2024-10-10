package pl.sknikod.kodemybackend.infrastructure.module.material_by_user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialSortField;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.SearchFields;
import pl.sknikod.kodemybackend.infrastructure.rest.UserControllerDefinition;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDefinition {
    private final MaterialGetByUserService materialGetByUserService;

    @Override
    public ResponseEntity<Page<MaterialPageable>> usersMaterials(
            Long authorId, int size, int page,
            MaterialSortField sortField, Sort.Direction sortDirection,
            SearchFields searchFields
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGetByUserService.getPersonalMaterials(
                        authorId,
                        Objects.requireNonNullElse(searchFields, new SearchFields()),
                        PageRequest.of(page, size, sortDirection, sortField.toString())
                ));
    }
}
