package pl.sknikod.kodemy.util;

import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;
import java.util.Optional;

public class AuditorAware implements org.springframework.data.domain.AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.of(Objects.requireNonNullElse(username, "Unknown"));
    }
}
