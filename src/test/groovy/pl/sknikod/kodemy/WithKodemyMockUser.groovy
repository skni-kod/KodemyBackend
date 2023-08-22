package pl.sknikod.kodemy

import org.springframework.core.annotation.AliasFor
import org.springframework.security.test.context.support.TestExecutionEvent
import org.springframework.security.test.context.support.WithSecurityContext
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName

import java.lang.annotation.*

@Target([ElementType.METHOD, ElementType.TYPE])
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@WithSecurityContext(factory = WithKodemyMockUserSecurityContextFactory.class)
@interface WithKodemyMockUser {
    String value() default "user";

    String username() default "";

    RoleName role() default RoleName.ROLE_USER;

    String[] authorities() default [];

    @AliasFor(annotation = WithSecurityContext.class)
    TestExecutionEvent setupBefore() default TestExecutionEvent.TEST_METHOD;
}