package pl.sknikod.kodemyauth.infrastructure.auth.handler;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.infrastructure.common.entity.UserPrincipal;
import pl.sknikod.kodemyauth.util.Base64Util;
import pl.sknikod.kodemyauth.util.Cookie;
import pl.sknikod.kodemyauth.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class AuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final SecurityConfig.SecurityProperties.AuthProperties authProperties;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        if (response.isCommitted()) return;

        String redirectUri = Option.of(request)
                .flatMap(req -> Option.of(Cookie.getCookie(req, authProperties.getKey().getRedirect()))
                        .map(v -> (String) Base64Util.decode(v))
                        .orElse(Option.of(req.getHeader(HttpHeaders.REFERER)))
                )
                .map(uri -> UriComponentsBuilder.fromUriString(uri).build().toString())
                .getOrNull();

        clearAuthenticationAttributes(request, response);
        Cookie.addCookie(response, authProperties.getKey().getJwt(), genBearer());
        getRedirectStrategy().sendRedirect(request, response, redirectUri == null ? "" : redirectUri);
    }

    public String genBearer() {
        return Option.of(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(UserPrincipal.class::isInstance)
                .map(UserPrincipal.class::cast)
                .map(user -> jwtUtil.generateToken(new JwtUtil.Input(
                        user.getId(),
                        user.getUsername(),
                        user.getAuthorities()
                )))
                .map(JwtUtil.Output::getBearer)
                .get();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        Option.of(request.getSession()).peek(session ->
                session.removeAttribute(authProperties.getKey().getCurrentSession()));
        Cookie.deleteCookie(request, response, authProperties.getKey().getRedirect());
    }
}