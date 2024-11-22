package pl.sknikod.kodemybackend.infrastructure.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AfterAction {
    Action action();

    enum Action {
        SAVE,
        UPDATE,
        STATUS_UPDATE
    }
}
