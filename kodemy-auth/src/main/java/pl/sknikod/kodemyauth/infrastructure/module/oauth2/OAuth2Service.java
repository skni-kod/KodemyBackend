package pl.sknikod.kodemyauth.infrastructure.module.oauth2;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Try;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.sknikod.kodemyauth.configuration.SecurityConfiguration;
import pl.sknikod.kodemyauth.infrastructure.database.model.User;
import pl.sknikod.kodemyauth.infrastructure.database.handler.UserStoreHandler;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.provider.OAuth2Provider;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2UserPrincipal;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final RestTemplate oAuth2RestTemplate;
    private final List<OAuth2Provider> oAuth2Providers;
    private final UserStoreHandler userStoreHandler;
    private final SecurityConfiguration.RoleProperties roleProperties;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return retrieveProviderUser(userRequest, oAuth2Providers.iterator())
                .map(this::createOrLoadUser)
                .map(this::toUserPrincipal)
                .orElse(null);
    }

    private Optional<OAuth2Provider.User> retrieveProviderUser(OAuth2UserRequest userRequest, Iterator<OAuth2Provider> iterator) {
        if (!iterator.hasNext()) {
            log.info("No OAuth2Provider found for registration ID: {}", userRequest.getClientRegistration().getRegistrationId());
            return Optional.empty();
        }
        OAuth2Provider provider = iterator.next();
        if (provider.supports(userRequest.getClientRegistration().getRegistrationId())) {
            log.info("Process {} provider class", provider.getClass().getSimpleName());
            return Optional.of(provider.retrieveUser(oAuth2RestTemplate, userRequest));
        }
        return retrieveProviderUser(userRequest, iterator); // check another one
    }

    private Tuple2<User, OAuth2Provider.User> createOrLoadUser(OAuth2Provider.User providerUser) {
        return userStoreHandler.findByProviderUser(providerUser)
                .fold(unused -> Tuple.of(this.createNewUser(providerUser), providerUser), user -> Tuple.of(user, providerUser));
    }

    private User createNewUser(OAuth2Provider.User providerUser) {
        return userStoreHandler.save(providerUser)
                .orElse(null);
    }

    private OAuth2UserPrincipal toUserPrincipal(Tuple2<User, OAuth2Provider.User> userTuple2) {
        return Try.of(() -> roleProperties.getAuthorities(userTuple2._1.getRole().getName().name()))
                .onFailure(th -> log.error("Cannot retrieve authorities for role", th))
                .fold(
                        unused -> map(userTuple2._1, Collections.emptySet(), userTuple2._2.getAttributes()),
                        authorities -> map(userTuple2._1, authorities, userTuple2._2.getAttributes())
                );
    }

    private OAuth2UserPrincipal map(User user, Set<SimpleGrantedAuthority> authorities, Map<String, Object> attributes) {
        return new OAuth2UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getIsExpired(),
                user.getIsLocked(),
                user.getIsCredentialsExpired(),
                user.getIsEnabled(),
                authorities,
                attributes
        );
    }
}
