package pl.sknikod.kodemyauth.infrastructure.auth;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.util.Base64Util;
import pl.sknikod.kodemyauth.util.CookieUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthController.REDIRECT_URI_PARAMETER;

@Repository
@RequiredArgsConstructor
public class AuthorizationRequestRepositoryImpl implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private final SecurityConfig.SecurityProperties.AuthProperties authProperties;

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return Option.of(request.getSession())
                .map(session -> Option.of(session.getAttribute(authProperties.getKey().getCurrentSession())).getOrNull())
                .map(Object::toString)
                .map(session -> (OAuth2AuthorizationRequest) Base64Util.decode(session))
                .getOrNull();
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAMETER);
        Option.of(request.getSession()).peek(sessionObj -> sessionObj.setAttribute(
                authProperties.getKey().getCurrentSession(),
                Base64Util.encode(authorizationRequest)
        ));

        if (Strings.isNotEmpty(redirectUriAfterLogin)) {
            CookieUtil.addCookie(response, authProperties.getKey().getRedirect(),
                    Base64Util.encode(redirectUriAfterLogin), 5 * 60, true);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return removeAuthorizationRequest(request, null);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        OAuth2AuthorizationRequest removedRequest = loadAuthorizationRequest(request);
        Option.of(request.getSession()).peek(session ->
                session.removeAttribute(authProperties.getKey().getCurrentSession())
        );
        return removedRequest;
    }
}
