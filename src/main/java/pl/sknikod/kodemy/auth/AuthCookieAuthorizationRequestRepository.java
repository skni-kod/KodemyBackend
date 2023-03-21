package pl.sknikod.kodemy.auth;

//import liquibase.repackaged.org.apache.commons.lang3.StringUtils;

import com.nimbusds.oauth2.sdk.util.StringUtils;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;
import pl.sknikod.kodemy.util.Cookie;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Base64;
import java.util.Optional;

import static pl.sknikod.kodemy.auth.AuthController.REDIRECT_URI_PARAMETER;

@Repository
public class AuthCookieAuthorizationRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String REDIRECT_URI_COOKIE_NAME = "kodemy_uri";
    public static final String AUTHORIZATION_REQUEST_COOKIE_NAME = "kodemy_sess";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return Optional.ofNullable(Cookie.getCookie(request, AUTHORIZATION_REQUEST_COOKIE_NAME))
                .map(cookie -> Base64.getUrlDecoder().decode(cookie))
                .map(authReq -> (OAuth2AuthorizationRequest) SerializationUtils.deserialize(authReq))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            Cookie.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE_NAME);
            Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE_NAME);
            return;
        }
        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAMETER);
        String authRequestEncoded = Base64.getUrlEncoder().encodeToString(
                SerializationUtils.serialize(authorizationRequest)
        );
        Cookie.addCookie(response, AUTHORIZATION_REQUEST_COOKIE_NAME, authRequestEncoded, 5 * 60);
        if (StringUtils.isNotBlank(redirectUriAfterLogin)) {
            Cookie.addCookie(response, REDIRECT_URI_COOKIE_NAME, redirectUriAfterLogin, 5 * 60);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE_NAME);
        Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE_NAME);
    }
}