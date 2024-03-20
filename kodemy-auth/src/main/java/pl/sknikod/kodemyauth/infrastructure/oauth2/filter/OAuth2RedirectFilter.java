package pl.sknikod.kodemyauth.infrastructure.oauth2.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.infrastructure.oauth2.OAuth2Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Map;
@Component
public class OAuth2RedirectFilter extends OAuth2AuthorizationRequestRedirectFilter {
    @SneakyThrows
    public OAuth2RedirectFilter(
            SecurityConfig.SecurityProperties.AuthProperties authProperties,
            ClientRegistrationRepository clientRegistrationRepository,
            AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository
    ) {
        super(new DefaultOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository,
                authProperties.getUri().getLogin()
        ));
        super.setAuthorizationRequestRepository(authorizationRequestRepository);
        Field field = OAuth2AuthorizationRequestRedirectFilter.class
                .getDeclaredField("authorizationRedirectStrategy");
        field.setAccessible(true);
        field.set(this, new RedirectStrategyImpl());
    }

    @AllArgsConstructor
    public static class RedirectStrategyImpl implements RedirectStrategy {
        @Override
        public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.toString());

            var rootNode = JsonNodeFactory.instance.objectNode()
                    .put("authUri", url);
            response.getWriter().write(rootNode.toPrettyString());
        }
    }
}
