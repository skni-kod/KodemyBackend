package pl.sknikod.kodemybackend.infrastructure.module.material;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemybackend.infrastructure.module.material.model.MaterialPageable;
import pl.sknikod.kodemybackend.infrastructure.rest.MaterialControllerDefinition;
import pl.sknikod.kodemybackend.infrastructure.store.MaterialStore;
import pl.sknikod.kodemybackend.infrastructure.store.UserStore;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialManageService {

    private final MaterialStore materialStore;

    public Page<MaterialPageable> manage(
            @NotNull MaterialControllerDefinition.MaterialFilterSearchParams materialFilterSearchParams,
            PageRequest pageRequest
    ) {
        return materialStore.findAll(createFindAllFilters(materialFilterSearchParams, pageRequest))
                .map(tuple -> tuple.map2(users -> 
                        users.stream().collect(Collectors.toMap(UserStore.User::getId, UserStore.User::getUsername)))
                )
                .mapTry(tuple -> tuple._1.map(object -> MaterialPageable.map(
                        object.getMaterial(), object.getAvgGrade(), tuple._2().get(object.getMaterial().getUserId())
                )))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }

    private MaterialStore.FindAllFilters createFindAllFilters(
            MaterialControllerDefinition.MaterialFilterSearchParams params,
            PageRequest pageRequest
    ) {
        return MaterialStore.FindAllFilters.builder()
                .phrase(params.getPhrase())
                .id(params.getId())
                .sectionId(params.getSectionId())
                .categoryIds(params.getCategoryIds())
                .pageRequest(pageRequest)
                .build();
    }
}
