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
import pl.sknikod.kodemybackend.infrastructure.module.material.model.FilterSearchParams;
import pl.sknikod.kodemybackend.infrastructure.rest.UserControllerDefinition;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserController implements UserControllerDefinition {
    private final MaterialGetByUserService materialGetByUserService;

    @Override
    public ResponseEntity<Page<MaterialPageable>> usersMaterials(
            Long userId, int size, int page,
            MaterialSortField sortField, Sort.Direction sortDirection,
            FilterSearchParams filterSearchParams
    ) {
        return ResponseEntity.status(HttpStatus.OK)
                .body(materialGetByUserService.getPersonalMaterials(
                        userId,
                        Objects.requireNonNullElse(filterSearchParams, new FilterSearchParams()),
                        PageRequest.of(page, size, sortDirection, sortField.getField())
                ));
    }
}
