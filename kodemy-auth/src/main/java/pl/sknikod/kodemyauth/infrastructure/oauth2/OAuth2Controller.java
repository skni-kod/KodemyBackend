package pl.sknikod.kodemyauth.infrastructure.oauth2;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Controller;
import pl.sknikod.kodemyauth.configuration.SecurityConfig.SecurityProperties.AuthProperties;
import pl.sknikod.kodemyauth.infrastructure.AuthDetails;
import pl.sknikod.kodemyauth.util.CookieUtil;
import pl.sknikod.kodemyauth.util.ServerController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class OAuth2Controller extends ServerController {
    private final AuthProperties authProperties;

    @SneakyThrows
    public void successLoginResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        var authDetails = (AuthDetails) authentication.getDetails();
        String accessToken = authDetails.getAccessToken();
        String refreshToken = authDetails.getRefreshToken();
        setJsonResponseHeaders(response);
        response.setStatus(HttpServletResponse.SC_OK);
        var rootNode = JsonNodeFactory.instance.objectNode()
                .put("accessToken", accessToken)
                .put("refreshToken", refreshToken);
        response.getWriter().write(rootNode.toPrettyString());
    }

    @SneakyThrows
    public void failureLoginResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException ex) {
        setJsonResponseHeaders(response);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        var rootNode = JsonNodeFactory.instance.objectNode()
                .put("error", ex.getMessage());
        response.getWriter().write(rootNode.toPrettyString());
    }

    public void successLogoutResponse(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
