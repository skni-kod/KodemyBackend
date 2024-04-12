package pl.sknikod.kodemybackend.infrastructure.common;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;

import java.util.Optional;

@Component
@AllArgsConstructor
public class ContextUtil {
    public Optional<SecurityConfig.UserPrincipal> getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && !(authentication.getPrincipal() instanceof String)) {
            return Optional.of((SecurityConfig.UserPrincipal) authentication.getPrincipal());
        }

        return Optional.empty();
    }

}
