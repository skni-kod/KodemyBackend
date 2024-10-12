package pl.sknikod.kodemycommons.data;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.sknikod.kodemycommons.security.UserPrincipal;

import java.util.Optional;

public class AuditorAware {
    public static Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getPrincipal)
                .filter(UserPrincipal.class::isInstance)
                .map(principal -> ((UserPrincipal) principal).getId())
                .or(Optional::empty);
    }
}
