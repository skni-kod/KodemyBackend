package pl.sknikod.kodemysearch.util.filter;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.sknikod.kodemysearch.configuration.SecurityConfig;
import pl.sknikod.kodemysearch.util.Cookie;
import pl.sknikod.kodemysearch.util.JwtUtil;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Value("${app.security.auth.key.jwt}")
    private String jwtKey;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (Strings.isEmpty(token) || (Strings.isNotEmpty(token) && !jwtUtil.isTokenValid(token)))
                throw new RuntimeException("JWT empty or invalid");
            var userPrincipal = map(jwtUtil.deserializeToken(token));
            var authentication = new UsernamePasswordAuthenticationToken(
                    userPrincipal, null, userPrincipal.getAuthorities()
            );
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error("Cannot set user authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (Strings.isNotEmpty(headerAuth) && headerAuth.startsWith("Bearer "))
            return headerAuth.substring(7);
        return Cookie.getCookie(request, jwtKey);
    }

    private SecurityConfig.UserPrincipal map(JwtUtil.Output.Deserialize deserializedObject) {
        var deserializedAuthorities = deserializedObject.getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
        return new SecurityConfig.UserPrincipal(
                deserializedObject.getId(),
                deserializedObject.getUsername(),
                deserializedAuthorities
        );
    }
}

