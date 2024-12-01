package pl.sknikod.kodemyauth.infrastructure.module.auth;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.util.web.BearerHelper;
import pl.sknikod.kodemycommons.exception.InternalError500Exception;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccessTokenService implements BearerHelper {
    public String getAccessToken(String authorizationHeader) {
        return Option.of(authorizationHeader)
                .map(this::extractBearer)
                .getOrElseThrow(InternalError500Exception::new);
    }
}
