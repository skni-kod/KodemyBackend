package pl.sknikod.kodemysearch.util;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface WithUserPrincipal {
    long id() default 1L;

    String username() default "username";

    String[] authorities() default {};

    boolean authenticated() default true;
}