package pl.sknikod.kodemyauth.infrastructure.auth;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthInfo;
import pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthResponse;
import pl.sknikod.kodemyauth.infrastructure.common.entity.User;
import pl.sknikod.kodemyauth.infrastructure.common.entity.UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.user.UserPrincipalUseCase;
import pl.sknikod.kodemyauth.infrastructure.user.rest.UserInfoResponse;
import pl.sknikod.kodemyauth.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Consumer;

import static pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthController.REDIRECT_URI_PARAMETER;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {
    private final UserPrincipalUseCase userPrincipalUseCase;
    private final JwtUtil jwtUtil;

    public String getLink(HttpServletRequest request, Consumer<UriComponentsBuilder> uriBuilderConsumer, String redirectUri) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
                .scheme(request.getScheme())
                .host(request.getServerName())
                .port(request.getServerPort())
                .queryParam(REDIRECT_URI_PARAMETER, redirectUri.length() > 8 ? redirectUri : "");
        uriBuilderConsumer.accept(uriComponentsBuilder);
        return uriComponentsBuilder.toUriString().toLowerCase();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            return userPrincipalUseCase.execute(super.loadUser(userRequest), userRequest.getClientRegistration().getRegistrationId());
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    public AuthInfo isAuthenticated() {
        AuthInfo authInfo = new AuthInfo();
        authInfo.setAuth(!SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal()
                .equals("anonymousUser")
        );
        return authInfo;
    }

    public AuthResponse getSessionToken() {
        String bearer = Option.of(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getPrincipal)
                .filter(principal -> principal instanceof UserPrincipal)
                .map(o -> (UserPrincipal) o)
                .map(user -> jwtUtil.generateToken(new JwtUtil.Input(
                        user.getId(),
                        user.getUsername(),
                        user.getAuthorities()
                )))
                .map(JwtUtil.Output::getBearer)
                .getOrNull();
        return new AuthResponse(bearer);
    }

    @Mapper(componentModel = "spring")
    public interface AuthMapper {
        UserInfoResponse map(User user);
    }
}