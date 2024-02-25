package pl.sknikod.kodemyauth.infrastructure.auth;

import io.vavr.control.Option;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
import pl.sknikod.kodemyauth.configuration.SecurityConfig;
import pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthInfo;
import pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthResponse;
import pl.sknikod.kodemyauth.infrastructure.auth.rest.OAuth2LinksResponse;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;
import pl.sknikod.kodemyauth.infrastructure.common.entity.User;
import pl.sknikod.kodemyauth.infrastructure.common.entity.UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.user.UserPrincipalUseCase;
import pl.sknikod.kodemyauth.infrastructure.user.rest.UserInfoResponse;
import pl.sknikod.kodemyauth.util.JwtUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

import static pl.sknikod.kodemyauth.infrastructure.auth.rest.AuthController.REDIRECT_URI_PARAMETER;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {
    private final UserPrincipalUseCase userPrincipalUseCase;
    private final SecurityConfig.SecurityProperties.AuthProperties authProperties;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    public OAuth2LinksResponse getLinks(String redirectUri, HttpServletRequest request) {
        var uriComponentsBuilder = UriComponentsBuilder.newInstance();
        if (!StringUtils.isEmpty(redirectUri))  uriComponentsBuilder.queryParam(REDIRECT_URI_PARAMETER, redirectUri);
        var uri = uriComponentsBuilder.build().toUri();
        var providersDetails = Arrays.stream(Provider.ProviderType.values())
                .map(provider -> new OAuth2LinksResponse.ProviderDetails(
                        provider,
                        UriComponentsBuilder.fromUri(uri).path(authProperties.getUri().getLogin()).path("/")
                                .path(provider.toString().toLowerCase()).build().toUriString()
                ))
                .toList();
        return authMapper.map(
                UriComponentsBuilder.fromUri(uri).path(authProperties.getUri().getLogout()).build().toUriString(),
                providersDetails
        );
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            return userPrincipalUseCase.execute(
                    super.loadUser(userRequest), userRequest.getClientRegistration().getRegistrationId()
            );
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

        default OAuth2LinksResponse map(String logoutUri, List<OAuth2LinksResponse.ProviderDetails> providersDetails) {
            return new OAuth2LinksResponse(logoutUri, providersDetails);
        }
    }
}