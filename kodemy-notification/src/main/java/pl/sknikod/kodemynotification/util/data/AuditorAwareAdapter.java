package pl.sknikod.kodemynotification.util.data;

import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;
import org.springframework.security.core.context.SecurityContextHolder;

import java.security.Principal;
import java.util.Optional;
import java.util.function.Supplier;

public class AuditorAwareAdapter implements AuditorAware<String> {
    private static final Supplier<Optional<String>> ANONYMOUS_SUPPLIER =
            () -> Optional.of("ANONYMOUS");

    @Override
    @NonNull
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Principal::getName)
                .map(String::toUpperCase)
                .or(ANONYMOUS_SUPPLIER);
    }
}
