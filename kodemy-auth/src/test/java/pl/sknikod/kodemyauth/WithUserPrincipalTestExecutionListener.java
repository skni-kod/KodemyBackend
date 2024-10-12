package pl.sknikod.kodemyauth;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import pl.sknikod.kodemycommons.security.UserPrincipal;

import java.util.Arrays;
import java.util.Collection;

public class WithUserPrincipalTestExecutionListener extends DependencyInjectionTestExecutionListener {
    @Override
    public void beforeTestMethod(@NonNull TestContext testContext) throws Exception {
        super.beforeTestMethod(testContext);
        var withUserPrincipal = testContext.getTestMethod().getAnnotation(WithUserPrincipal.class);
        if (withUserPrincipal == null)
            withUserPrincipal = testContext.getTestClass().getAnnotation(WithUserPrincipal.class);

        if (withUserPrincipal != null) {
            final var userPrincipal = toUserPrincipal(withUserPrincipal);
            final var authenticationToken = withUserPrincipal.authenticated() ?
                    new UsernamePasswordAuthenticationToken(userPrincipal, null, userPrincipal.getAuthorities()) :
                    new UsernamePasswordAuthenticationToken(userPrincipal, null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
    }

    private UserPrincipal toUserPrincipal(WithUserPrincipal withUserPrincipal) {
        return new UserPrincipal(
                withUserPrincipal.id(),
                withUserPrincipal.username(),
                toAuthorities(withUserPrincipal.authorities())
        );
    }

    private Collection<SimpleGrantedAuthority> toAuthorities(String[] authorities) {
        return Arrays.stream(authorities).map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public void afterTestMethod(@NonNull TestContext testContext) throws Exception {
        super.afterTestMethod(testContext);
        var isAnnotation = testContext.getTestMethod().isAnnotationPresent(WithUserPrincipal.class);
        if (!isAnnotation)
            isAnnotation = testContext.getTestClass().isAnnotationPresent(WithUserPrincipal.class);
        if (isAnnotation)
            SecurityContextHolder.clearContext();
    }
}