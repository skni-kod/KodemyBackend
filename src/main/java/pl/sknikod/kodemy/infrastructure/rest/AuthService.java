package pl.sknikod.kodemy.infrastructure.rest;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import pl.sknikod.kodemy.infrastructure.model.entity.User;
import pl.sknikod.kodemy.infrastructure.model.entity.UserProviderType;
import pl.sknikod.kodemy.infrastructure.rest.model.UserInfoResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Consumer;

import static pl.sknikod.kodemy.infrastructure.rest.AuthController.REDIRECT_URI_PARAMETER;

@Service
@RequiredArgsConstructor
public class AuthService extends DefaultOAuth2UserService {
    private final UserPrincipalUseCase userPrincipalUseCase;

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

    public UserProviderType[] getProvidersList() {
        return UserProviderType.values();
    }

    @Mapper(componentModel = "spring")
    public interface AuthMapper {
        UserInfoResponse map(User user);
    }
}