package pl.sknikod.kodemyauth.infrastructure.auth;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import io.vavr.control.Option;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;
import pl.sknikod.kodemyauth.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

import static pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthController.REDIRECT_URI_PARAMETER;

@Repository
public class AuthorizationRequestRepositoryImpl implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String REDIRECT_URI_COOKIE_NAME = "kodemy_uri";
    public static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "kodemy_sess";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return Optional.ofNullable(request.getSession())
                .map(session -> Option.of(session.getAttribute(AUTHORIZATION_REQUEST_COOKIE_NAME)).getOrNull())
                .map(Object::toString)
                .map(cookie -> Base64.getUrlDecoder().decode(cookie))
                .map(authReq -> (OAuth2AuthorizationRequest) SerializationUtils.deserialize(authReq))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequest(request, response);
            return;
        }

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAMETER);
        Option.of(request.getSession()).peek(sessionObj -> {
            String authRequestEncoded = Base64.getUrlEncoder().encodeToString(
                    SerializationUtils.serialize(authorizationRequest)
            );
            sessionObj.setAttribute(AUTHORIZATION_REQUEST_COOKIE_NAME, authRequestEncoded);
        });

        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            Cookie.addCookie(response, REDIRECT_URI_COOKIE_NAME, redirectUriAfterLogin, 5 * 60);
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
        removeAuthorizationSession(request, response);
        return removedRequest;
    }

    public void removeAuthorizationSession(HttpServletRequest request, HttpServletResponse response) {
        Option.of(request.getSession()).peek(session -> session.removeAttribute(AUTHORIZATION_REQUEST_COOKIE_NAME));
        Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE_NAME);
    }
}
