package pl.sknikod.kodemyauth.infrastructure.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.exception.ExceptionRestGenericMessage;
import pl.sknikod.kodemyauth.util.Base64Util;
import pl.sknikod.kodemyauth.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OAuth2Controller {
    private final ObjectMapper objectMapper;
    private final SecurityConfig.SecurityProperties.AuthProperties authProperties;

    @SneakyThrows
    public void redirectStrategyResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            String url
    ) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.toString());
        response.getWriter().write(objectMapper.writeValueAsString(Map.of(
                "redirectUrl", url
        )));
    }

    @SneakyThrows
    public void successResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String redirectUri = Option.ofOptional(CookieUtil.get(request.getCookies(), authProperties.getKey().getRedirect()))
                .map(v -> (String) Base64Util.decode(v))
                .orElse(Option.of(request.getHeader(HttpHeaders.REFERER)))
                .map(uri -> UriComponentsBuilder.fromUriString(uri).build().toString())
                .getOrNull();
        response.sendRedirect(redirectUri);
    }

    @SneakyThrows
    public void failureResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex) {
        String redirectUri = Option.ofOptional(CookieUtil.get(request.getCookies(), authProperties.getKey().getRedirect()))
                .map(v -> (String) Base64Util.decode(v))
                .orElse(Option.of(request.getHeader(HttpHeaders.REFERER)))
                .map(uri -> UriComponentsBuilder.fromUriString(uri).queryParam("error", ex.getMessage()).build().toString())
                .getOrNull();
        response.sendRedirect(redirectUri);
    }
}
