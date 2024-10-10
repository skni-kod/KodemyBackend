package pl.sknikod.kodemycommon.data;

import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.sknikod.kodemycommon.security.UserPrincipal;

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
