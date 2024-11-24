package pl.sknikod.kodemysearch.infrastructure.event.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemysearch.infrastructure.module.material.MaterialGradeService;
import pl.sknikod.kodemysearch.infrastructure.module.material.model.MaterialAvgGradeEvent;

import java.util.function.Consumer;

@Slf4j
@Component("materialGradeUpdated")
@RequiredArgsConstructor
public class MaterialGradeUpdatedConsumer implements Consumer<MaterialAvgGradeEvent> {

    private final MaterialGradeService materialGradeService;

    @Override
    public void accept(MaterialAvgGradeEvent event) {
        log.info("Consuming message from materialGradeUpdated");
        materialGradeService.updateAvgGrade(event);
    }
}
