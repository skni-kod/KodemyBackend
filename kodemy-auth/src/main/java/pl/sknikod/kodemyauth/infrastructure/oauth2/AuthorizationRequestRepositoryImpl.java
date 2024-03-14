package pl.sknikod.kodemyauth.infrastructure.oauth2;

import io.vavr.control.Option;
import lombok.NonNull;
import org.apache.logging.log4j.util.Strings;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.util.Base64Util;
import pl.sknikod.kodemyauth.util.CookieUtil;
import pl.sknikod.kodemyauth.util.EncryptionUtil;

import javax.crypto.SecretKey;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.time.Duration;

import static pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthController.REDIRECT_URI_PARAMETER;

@Repository
public class AuthorizationRequestRepositoryImpl implements AuthorizationRequestRepository<OAuth2AuthorizationRequest> {
    private final SecurityConfig.SecurityProperties.AuthProperties authProperties;
    private final SecretKey encryptionKey;

    public AuthorizationRequestRepositoryImpl(SecurityConfig.SecurityProperties.AuthProperties authProperties) {
        this.authProperties = authProperties;
        this.encryptionKey = EncryptionUtil.generateKey(authProperties.getPassword(), new byte[]{0});
    }

    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtil.get(request.getCookies(), authProperties.getKey().getCurrentSession())
                .map(session -> Base64Util.decode(session, bytes -> EncryptionUtil.decrypt(encryptionKey, bytes)))
                .map(OAuth2AuthorizationRequest.class::cast)
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        String currentSessionKey = authProperties.getKey().getCurrentSession();
        if (authorizationRequest == null) {
            CookieUtil.expire(request.getCookies(), currentSessionKey)
                    .ifPresent(response::addCookie);
            return;
        }

        String redirectUriAfterLogin = request.getParameter(REDIRECT_URI_PARAMETER);
        if (Strings.isNotEmpty(redirectUriAfterLogin)) {
            Cookie redirect = CookieUtil.generate(authProperties.getKey().getRedirect(),
                    Base64Util.encode(redirectUriAfterLogin), Duration.ofMinutes(5));
            response.addCookie(redirect);
        }

        Cookie session = CookieUtil.generate(
                currentSessionKey,
                Base64Util.encode(authorizationRequest, bytes -> EncryptionUtil.encrypt(encryptionKey, bytes)),
                Duration.ofMinutes(5)
        );
        response.addCookie(session);
    }

    @Override
    @SuppressWarnings("deprecated")
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request) {
        return loadAuthorizationRequest(request);
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return removeAuthorizationRequest(request);
    }
}
