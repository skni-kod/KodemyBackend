package pl.sknikod.kodemybackend.infrastructure.material;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.common.mapper.MaterialMapper;
import pl.sknikod.kodemybackend.infrastructure.common.repository.MaterialRepository;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SearchFields;
import pl.sknikod.kodemybackend.infrastructure.material.rest.SingleMaterialResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class MaterialUserGetUseCase {
    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    public Page<SingleMaterialResponse> search(SearchFields searchFields, PageRequest pageRequest) {
        return new PageImpl<>(
                materialRepository
                        .searchMaterials(searchFields, pageRequest)
                        .stream()
                        .map(materialMapper::map)
                        .toList(),
                pageRequest,
                materialRepository.count()
        );
    }
}
