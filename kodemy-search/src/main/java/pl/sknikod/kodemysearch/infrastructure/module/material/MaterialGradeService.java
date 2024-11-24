package pl.sknikod.kodemysearch.infrastructure.module.material;

import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;
import pl.sknikod.kodemycommons.exception.content.ExceptionUtil;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialAvgGradeEvent;
import pl.sknikod.kodemysearch.infrastructure.store.MaterialSearchStore;

@Slf4j
@Service
@RequiredArgsConstructor
@DependsOn("rabbitConfiguration")
public class MaterialGradeService {
    private final MaterialSearchStore materialSearchStore;

    public void updateAvgGrade(MaterialAvgGradeEvent event) {
        Try.of(() -> materialSearchStore.update(event.getId(), event.getAvgGrade()))
                .getOrElseThrow(ExceptionUtil::throwIfFailure);
    }
}
