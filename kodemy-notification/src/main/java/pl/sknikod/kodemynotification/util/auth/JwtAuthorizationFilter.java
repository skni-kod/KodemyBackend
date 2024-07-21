package pl.sknikod.kodemynotification.util.auth;

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

@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final List<AntPathRequestMatcher> notFilterMatchers;
    private final JwtService jwtService;

    public JwtAuthorizationFilter(List<String> permitPaths, JwtService jwtService) {
        this.notFilterMatchers = permitPaths.stream().map(AntPathRequestMatcher::new).toList();
        this.jwtService = jwtService;
    }

    public JwtAuthorizationFilter(JwtService jwtService) {
        this(Collections.emptyList(), jwtService);
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
                .flatMapTry(jwtService::parseToken)
                .map(this::toUserPrincipal)
                .onFailure(th -> log.debug("Authenticate failure", th))
                .onSuccess(user -> authenticate(request, user));
        filterChain.doFilter(request, response);
    }

    private Try<String> extractBearer(HttpServletRequest request) {
        return Option.of(request.getHeader(HttpHeaders.AUTHORIZATION))
                .toTry(() -> new RuntimeException("Authorization header is empty or invalid"))
                .onFailure(th -> log.debug(th.getMessage()))
                .filter(v -> v.startsWith("Bearer "))
                .map(header -> header.substring(7));
    }

    private UserPrincipal toUserPrincipal(JwtService.Token.Deserialize claims) {
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

