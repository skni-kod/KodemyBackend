package pl.sknikod.kodemybackend.infrastructure.aspect;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.database.Grade;
import pl.sknikod.kodemybackend.infrastructure.database.Material;
import pl.sknikod.kodemybackend.infrastructure.event.producer.MaterialCreatedProducer;
import pl.sknikod.kodemybackend.infrastructure.event.producer.MaterialGradeUpdatedProducer;
import pl.sknikod.kodemybackend.infrastructure.event.producer.MaterialStatusUpdatedProducer;
import pl.sknikod.kodemybackend.infrastructure.event.producer.MaterialUpdatedProducer;
import pl.sknikod.kodemybackend.infrastructure.store.GradeStore;
import pl.sknikod.kodemybackend.infrastructure.store.UserStore;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AfterActionAspect {
    private final GradeStore gradeStore;
    private final UserStore userStore;
    private final MaterialCreatedProducer materialCreatedProducer;
    private final MaterialUpdatedProducer materialUpdatedProducer;
    private final MaterialStatusUpdatedProducer materialStatusUpdatedProducer;
    private final MaterialGradeUpdatedProducer materialGradeUpdatedProducer;

    @Pointcut("@annotation(afterAction)")
    public void afterActionPointcut(AfterAction afterAction) {
        // placeholder
    }

    @SuppressWarnings("unchecked")
    @AfterReturning(value = "afterActionPointcut(afterAction)", returning = "result", argNames = "afterAction,result")
    public void executeAfterAction(AfterAction afterAction, Object result) {
        switch (afterAction.action()) {
            case SAVE -> afterSave((Try<Material>) result);
            case UPDATE -> afterUpdate((Try<Material>) result);
            case STATUS_UPDATE -> afterStatusUpdate((Tuple2<Long, Material.MaterialStatus>) result);
            case GRADE_ADD -> afterGradeAdd((Try<Grade>) result);
        }
    }

    private void afterSave(Try<Material> result) {
        log.info("After save action executed");
        result
                .filter(material -> Material.MaterialStatus.APPROVED.equals(material.getStatus()))
                .mapTry(material -> {
                    UserStore.User user = userStore.findById(material.getUserId()).get();
                    Double avgGrade = gradeStore.findAvgGradeByMaterial(material.getUserId()).get();
                    return Tuple.of(material, avgGrade, user);
                })
                .mapTry(tuple -> MaterialCreatedProducer.Message.map(tuple._1, tuple._3.getUsername()))
                .peek(materialCreatedProducer::publish);
    }

    private void afterUpdate(Try<Material> result) {
        log.info("After update action executed");
        result.filter(material -> Material.MaterialStatus.APPROVED.equals(material.getStatus()))
                .mapTry(material -> {
                    UserStore.User user = userStore.findById(material.getUserId()).get();
                    Double avgGrade = gradeStore.findAvgGradeByMaterial(material.getUserId()).get();
                    return Tuple.of(material, avgGrade, user);
                })
                .mapTry(tuple -> MaterialUpdatedProducer.Message.map(tuple._1, tuple._2, tuple._3.getUsername()))
                .peek(materialUpdatedProducer::publish);
    }

    private void afterStatusUpdate(Tuple2<Long, Material.MaterialStatus> result) {
        log.info("After status update action executed");
        Try.of(() -> new MaterialStatusUpdatedProducer.Message(result._1, result._2))
                .peek(materialStatusUpdatedProducer::publish);
    }

    private void afterGradeAdd(Try<Grade> result) {
        log.info("After grade add action executed");
        result.flatMap(grade -> gradeStore.findAvgGradeByMaterial(grade.getMaterial().getId())
                .map(avgGrade -> Tuple.of(grade.getMaterial().getId(), avgGrade)))
                .map(tuple -> new MaterialGradeUpdatedProducer.Message(tuple._1, tuple._2))
                .peek(materialGradeUpdatedProducer::publish);
    }
}
