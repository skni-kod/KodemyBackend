package pl.sknikod.kodemyauth.util.auth;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthFacade {
    private static SecurityContext getContext(){
        return SecurityContextHolder.getContext();
    }

    public static Optional<Authentication> getAuthentication() {
        return Optional.ofNullable(getContext().getAuthentication());
    }

    public static boolean isAuthenticated() {
        return getAuthentication()
                .filter(Authentication::isAuthenticated)
                .map(Authentication::isAuthenticated)
                .orElse(false);
    }

    @Nullable
    public static String getCurrentUsername() {
        return getAuthentication()
                .map(Principal::getName)
                .orElse(null);
    }

    public static Optional<UserPrincipal> getCurrentUserPrincipal() {
        return getAuthentication()
                .flatMap(AuthFacade::getCurrentUserPrincipal);
    }

    public static Optional<UserPrincipal> getCurrentUserPrincipal(Authentication authentication) {
        return Optional.ofNullable(authentication)
                .map(Authentication::getPrincipal)
                .filter(UserPrincipal.class::isInstance)
                .map(UserPrincipal.class::cast);
    }

    public static Authentication setAuthentication(Authentication authentication, boolean isClearContext) {
        SecurityContext securityContext = isClearContext ? SecurityContextHolder.createEmptyContext() : getContext();
        securityContext.setAuthentication(authentication);
        return authentication;
    }

    public static Authentication setAuthentication(Authentication authentication) {
        return setAuthentication(authentication, false);
    }
}