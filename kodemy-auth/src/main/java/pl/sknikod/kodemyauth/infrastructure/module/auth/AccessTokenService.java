package pl.sknikod.kodemyauth.infrastructure.module.auth;

import io.vavr.control.Option;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenService {
    public Object getAccessToken(String authorizationHeader) {
        return Option.of(authorizationHeader)
                .filter(this::isBearerTokenPresent)
                .map(bearer -> bearer.substring(7))
                .getOrElseThrow(InternalError500Exception::new);
    }

    private boolean isBearerTokenPresent(@NonNull String authorizationHeader) {
        return authorizationHeader.startsWith("Bearer ");
    }
}
