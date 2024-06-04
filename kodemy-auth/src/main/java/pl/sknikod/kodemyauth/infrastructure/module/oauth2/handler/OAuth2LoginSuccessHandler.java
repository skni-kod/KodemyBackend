package pl.sknikod.kodemyauth.infrastructure.module.oauth2.handler;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import pl.sknikod.kodemyauth.exception.structure.ServerProcessingException;
import pl.sknikod.kodemyauth.infrastructure.database.entity.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenRepositoryHandler;
import pl.sknikod.kodemyauth.util.auth.AuthFacade;
import pl.sknikod.kodemyauth.util.auth.JwtService;
import pl.sknikod.kodemyauth.util.auth.UserPrincipal;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtService jwtService;
    private final String frontRouteBaseUrl;
    private final RefreshTokenRepositoryHandler refreshTokenRepositoryHandler;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response, Authentication authentication
    ) {
        final var params = postProcess(authentication)
                .<Map<String, String>>fold(
                        th -> Map.of("error", th.getMessage().toLowerCase().replace(" ", "_")),
                        tokens -> Map.of("token", tokens._1.value(), "refresh", tokens._2.getToken().toString())
                );
        ((RouteRedirectStrategy) getRedirectStrategy())
                .sendRedirect(request, response, frontRouteBaseUrl, params);
    }

    @SuppressWarnings("unchecked")
    private Try<Tuple2<JwtService.Token, RefreshToken>> postProcess(Authentication authentication) {
        return Try.of(() -> AuthFacade.getCurrentUserPrincipal(authentication)
                        .orElseThrow(ServerProcessingException::new))
                .onFailure(th -> log.error("Cannot get user principal", th))
                .flatMapTry(this::generateTokens)
                .recoverWith(th -> Try.failure(new ServerProcessingException()));
    }

    private Try<Tuple2<JwtService.Token, RefreshToken>> generateTokens(UserPrincipal userPrincipal) {
        return Try.of(() -> jwtService.generateUserToken(map(userPrincipal)))
                .flatMapTry(bearerToken -> refreshTokenRepositoryHandler
                        .createAndGet(userPrincipal.getId(), bearerToken.id())
                        .map(refreshToken -> Tuple.of(bearerToken, refreshToken)))
                .onFailure(th -> log.error("Error during tokens generation", th));
    }

    private JwtService.Input map(UserPrincipal user) {
        return new JwtService.Input(
                user.getId(),
                user.getUsername(),
                !user.isAccountNonExpired(),
                !user.isAccountNonLocked(),
                !user.isCredentialsNonExpired(),
                user.isEnabled(),
                user.getAuthorities()
        );
    }
}
