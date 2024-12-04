package pl.sknikod.kodemycommons.security;

import io.vavr.control.Option;
import io.vavr.control.Try;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final List<AntPathRequestMatcher> notFilterMatchers;
    private final JwtProvider jwtProvider;
    private static final Predicate<String> BEARER_HEADER_MATCHER;

    static {
        BEARER_HEADER_MATCHER = Pattern.compile("[Bb]earer .*\\..*\\..*").asMatchPredicate();
    }

    public JwtAuthorizationFilter(
            List<String> permitPaths,
            JwtProvider jwtProvider
    ) {
        this.notFilterMatchers = permitPaths.stream().map(AntPathRequestMatcher::new).toList();
        this.jwtProvider = jwtProvider;
    }

    public JwtAuthorizationFilter(JwtProvider jwtProvider) {
        this(Collections.emptyList(), jwtProvider);
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return notFilterMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        extractBearer(request)
                .flatMapTry(jwtProvider::parseToken)
                .map(this::toUserPrincipal)
                .onFailure(th -> log.debug("Authenticate failure", th))
                .onSuccess(user -> authenticate(request, user));
        filterChain.doFilter(request, response);
    }

    private Try<String> extractBearer(HttpServletRequest request) {
        return Option.of(request.getHeader(HttpHeaders.AUTHORIZATION))
                .toTry(() -> new RuntimeException("Authorization header is empty or invalid"))
                .onFailure(th -> log.debug(th.getMessage()))
                .filter(BEARER_HEADER_MATCHER)
                .map(header -> header.substring(7));
    }

    private UserPrincipal toUserPrincipal(JwtProvider.Token.Deserialize claims) {
        return new UserPrincipal(
                claims.getId(),
                claims.getUsername(),
                claims.isExpired(),
                claims.isLocked(),
                claims.isCredentialsExpired(),
                claims.isEnabled(),
                claims.getAuthorities()
        );
    }

    private void authenticate(HttpServletRequest request, UserPrincipal userPrincipal) {
        final var authToken = new UsernamePasswordAuthenticationToken(
                userPrincipal, null, userPrincipal.getAuthorities()
        );
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        AuthFacade.setAuthentication(authToken);
        log.debug("Authenticate successfully");
    }
}

