package pl.sknikod.kodemyauth.util.web;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public interface BearerHelper {
    default boolean isBearerTokenPresent(@NonNull String authorizationHeader) {
        return authorizationHeader.startsWith("Bearer ");
    }

    default @Nullable String extractBearer(@NonNull String authorizationHeader) {
        return isBearerTokenPresent(authorizationHeader) ? authorizationHeader.substring(7) : null;
    }
}
