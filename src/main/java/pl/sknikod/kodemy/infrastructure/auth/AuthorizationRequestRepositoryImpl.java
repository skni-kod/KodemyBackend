package pl.sknikod.kodemy.infrastructure.auth;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import io.vavr.control.Option;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

import static pl.sknikod.kodemy.infrastructure.rest.AuthController.REDIRECT_URI_PARAMETER;

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
        /*return Optional.ofNullable(Cookie.getCookie(request, AUTHORIZATION_REQUEST_COOKIE_NAME))
                .map(cookie -> Base64.getUrlDecoder().decode(cookie))
                .map(authReq -> (OAuth2AuthorizationRequest) SerializationUtils.deserialize(authReq))
                .orElse(null);*/
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            /*Cookie.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE_NAME);*/
            Option.of(request.getSession()).map(sessionObj -> {
                sessionObj.removeAttribute(AUTHORIZATION_REQUEST_COOKIE_NAME);
                return null;
            });
            Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE_NAME);
            return;
        }

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAMETER);
        //Cookie.addCookie(response, AUTHORIZATION_REQUEST_COOKIE_NAME, authRequestEncoded, 5 * 60);

        Option.of(request.getSession()).map(sessionObj -> {
            String authRequestEncoded = Base64.getUrlEncoder().encodeToString(
                    SerializationUtils.serialize(authorizationRequest)
            );
            sessionObj.setAttribute(AUTHORIZATION_REQUEST_COOKIE_NAME, authRequestEncoded);
            return null;
        });

        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            Cookie.addCookie(response, REDIRECT_URI_COOKIE_NAME, redirectUriAfterLogin, 5 * 60);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationSession(HttpServletRequest request, HttpServletResponse response) {
        //Cookie.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE_NAME);
        Option.of(request.getSession()).map(session -> {
            session.removeAttribute(AUTHORIZATION_REQUEST_COOKIE_NAME);
            return null;
        });
        Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE_NAME);
    }
}