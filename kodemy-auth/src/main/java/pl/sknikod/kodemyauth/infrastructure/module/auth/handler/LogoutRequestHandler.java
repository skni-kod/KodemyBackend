package pl.sknikod.kodemyauth.infrastructure.module.auth.handler;

import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import pl.sknikod.kodemyauth.infrastructure.module.auth.LogoutService;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.security.AuthFacade;
import pl.sknikod.kodemycommon.security.JwtProvider;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class LogoutRequestHandler implements LogoutHandler {
    private final LogoutService logoutService;
    private final JwtProvider jwtProvider;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        /*extractBearer(request)
                .flatMap(jwtProvider::extractJti)
                .flatMap(bearerJti -> postProcess(authentication, bearerJti))
                .onFailure(th -> log.error("Cannot logout", th));*/
    }

    @SuppressWarnings("unchecked")
    private Try<Boolean> postProcess(Authentication authentication, UUID bearerJti) {
        return Try.of(() -> AuthFacade.getCurrentUserPrincipal(authentication)
                        .orElseThrow(InternalError500Exception::new))
                .onFailure(th -> log.error("Cannot get user principal to logout", th))
                .mapTry(userPrincipal -> logoutService.logout(userPrincipal, bearerJti))
                .recoverWith(th -> Try.failure(new InternalError500Exception()));
    }

    private Try<String> extractBearer(HttpServletRequest request) {
        return Option.of(request.getHeader(HttpHeaders.AUTHORIZATION))
                .toTry(() -> new RuntimeException("Authorization header is empty or invalid"))
                .onFailure(th -> log.debug(th.getMessage()))
                .filter(v -> v.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }
}
