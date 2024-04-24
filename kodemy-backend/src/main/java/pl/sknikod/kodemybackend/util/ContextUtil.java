package pl.sknikod.kodemybackend.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ContextUtil {
    public static Optional<SecurityConfig.UserPrincipal> getCurrentUserPrincipal() {
        return Optional.of(SecurityContextHolder.getContext().getAuthentication())
                .filter(authentication -> !(authentication.getPrincipal() instanceof String))
                .map(authentication -> (SecurityConfig.UserPrincipal) authentication.getPrincipal());
    }

}
