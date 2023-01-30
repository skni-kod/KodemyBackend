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

@Repository
public class AuthSessionRequestRepository implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    public static final String REDIRECT_URI_COOKIE = "kodemy_uri";
    public static final String AUTHORIZATION_REQUEST_COOKIE = "kodemy_sess";

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        var authorizationRequest = Cookie.getCookie(request, AUTHORIZATION_REQUEST_COOKIE);
        if (authorizationRequest != null)
            return (OAuth2AuthorizationRequest) SerializationUtils.deserialize(
                    Base64.getUrlDecoder().decode(authorizationRequest)
            );
        return null;
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            Cookie.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE);
            Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE);
            return;
        }
        String redirect = request.getParameter(REDIRECT_URI_COOKIE);
        String authRequestEncoded = Base64.getUrlEncoder().encodeToString(
                SerializationUtils.serialize(authorizationRequest)
        );
        Cookie.addCookie(response, AUTHORIZATION_REQUEST_COOKIE, authRequestEncoded, 5 * 60);
        if (StringUtils.isNotBlank(redirect)) {
            Cookie.addCookie(response, REDIRECT_URI_COOKIE, redirect, 5 * 60);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationSession(HttpServletRequest request, HttpServletResponse response) {
        Cookie.deleteCookie(request, response, AUTHORIZATION_REQUEST_COOKIE);
        Cookie.deleteCookie(request, response, REDIRECT_URI_COOKIE);
    }
}