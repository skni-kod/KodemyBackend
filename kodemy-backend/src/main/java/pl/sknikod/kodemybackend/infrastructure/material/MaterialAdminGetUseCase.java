package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SearchFields;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialAdminGetUseCase {
    private final MaterialRepository materialRepository;
    private final MaterialPageableUtil materialPageableUtil;

    public Page<MaterialPageable> manage(@NotNull SearchFields searchFields, PageRequest page, Optional<SecurityConfig.UserPrincipal> principal) {
        principal.map(SecurityConfig.UserPrincipal::getAuthorities)
                .filter(v -> !v.contains(new SimpleGrantedAuthority("CAN_VIEW_ALL_MATERIALS")))
                .orElseThrow(() -> new AccessDeniedException("Access is denied"));

        Page<Object[]> materials = materialRepository.searchMaterialsWithAvgGrades(
                searchFields.getId(),
                searchFields.getPhrase(),
                searchFields.getStatuses(),
                searchFields.getCreatedBy(),
                searchFields.getSectionId(),
                searchFields.getCategoryIds(),
                searchFields.getTagIds(),
                null,
                searchFields.getCreatedDateFrom(),
                searchFields.getCreatedDateTo(),
                searchFields.getMinAvgGrade(),
                searchFields.getMaxAvgGrade(),
                page
        );
        return new PageImpl<>(
                materials.stream()
                        .map(material -> materialPageableUtil.map((Material) material[0], (Double) material[1]))
                        .toList(),
                page,
                materials.getTotalElements()
        );
    }
}
