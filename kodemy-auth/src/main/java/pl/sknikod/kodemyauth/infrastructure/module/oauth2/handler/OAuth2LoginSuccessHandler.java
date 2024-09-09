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
import pl.sknikod.kodemyauth.infrastructure.database.model.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.handler.RefreshTokenStoreHandler;
import pl.sknikod.kodemyauth.util.route.RouteRedirectStrategy;
import pl.sknikod.kodemycommon.exception.InternalError500Exception;
import pl.sknikod.kodemycommon.security.AuthFacade;
import pl.sknikod.kodemycommon.security.JwtProvider;
import pl.sknikod.kodemycommon.security.UserPrincipal;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtProvider jwtProvider;
    private final String frontRouteBaseUrl;
    private final RefreshTokenStoreHandler refreshTokenRepositoryHandler;

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
    private Try<Tuple2<JwtProvider.Token, RefreshToken>> postProcess(Authentication authentication) {
        return Try.of(() -> AuthFacade.getCurrentUserPrincipal(authentication)
                        .orElseThrow(InternalError500Exception::new))
                .onFailure(th -> log.error("Cannot get user principal", th))
                .flatMapTry(this::generateTokens)
                .recoverWith(th -> Try.failure(new InternalError500Exception()));
    }

    private Try<Tuple2<JwtProvider.Token, RefreshToken>> generateTokens(UserPrincipal userPrincipal) {
        return Try.of(() -> jwtProvider.generateUserToken(map(userPrincipal)))
                .flatMapTry(bearerToken -> refreshTokenRepositoryHandler
                        .createAndGet(userPrincipal.getId(), bearerToken.id())
                        .map(refreshToken -> Tuple.of(bearerToken, refreshToken)))
                .onFailure(th -> log.error("Error during tokens generation", th));
    }

    private JwtProvider.Input map(UserPrincipal user) {
        return new JwtProvider.Input(
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
