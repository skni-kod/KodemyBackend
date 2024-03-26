package pl.sknikod.kodemybackend.infrastructure.common;

import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;

@Component
@AllArgsConstructor
public class ContextUtil {
    public SecurityConfig.UserPrincipal getCurrentUserPrincipal() {
        return (SecurityConfig.UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
    }
}
