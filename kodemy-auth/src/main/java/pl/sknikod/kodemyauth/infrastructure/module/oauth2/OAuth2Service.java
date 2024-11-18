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
import org.springframework.stereotype.Service;
import pl.sknikod.kodemyauth.infrastructure.database.Permission;
import pl.sknikod.kodemyauth.infrastructure.database.Role;
import pl.sknikod.kodemyauth.infrastructure.database.RoleRepository;
import pl.sknikod.kodemyauth.infrastructure.database.User;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.OAuth2Provider;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.processor.OAuth2ProcessorResult;
import pl.sknikod.kodemyauth.infrastructure.module.oauth2.util.OAuth2UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.store.UserStore;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final RoleRepository roleRepository;
    private final List<OAuth2Provider> oAuth2Providers;
    private final UserStore userStore;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        return retrieveUser(userRequest, oAuth2Providers.iterator())
                .map(this::createOrLoadUser)
                .map(this::toUserPrincipal)
                .orElse(null);
    }

    private Optional<OAuth2ProcessorResult> retrieveUser(OAuth2UserRequest userRequest, Iterator<OAuth2Provider> iterator) {
        if (!iterator.hasNext()) {
            log.info("No processable provider for registration ID: {}", userRequest.getClientRegistration().getRegistrationId());
            return Optional.empty();
        }
        OAuth2Provider provider = iterator.next();
        if (provider.isApply(userRequest.getClientRegistration().getRegistrationId())) {
            log.info("Process {} provider class", provider.getClass().getSimpleName());
            return Optional.of(provider.retrieve(userRequest));
        }
        return retrieveUser(userRequest, iterator); // check another one
    }

    private Tuple2<User, OAuth2ProcessorResult> createOrLoadUser(OAuth2ProcessorResult providerUser) {
        return userStore.findByProviderUser(providerUser)
                .fold(unused -> Tuple.of(this.createNewUser(providerUser), providerUser), user -> Tuple.of(user, providerUser));
    }

    private User createNewUser(OAuth2ProcessorResult providerUser) {
        return userStore.save(providerUser)
                .orElse(null);
    }

    private OAuth2UserPrincipal toUserPrincipal(Tuple2<User, OAuth2ProcessorResult> userTuple2) {
        return Try.of(() -> roleRepository.findById(userTuple2._1.getRole().getId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(Role::getPermissions)
                .onFailure(th -> log.error("Cannot retrieve authorities for role", th))
                .fold(
                        unused -> map(userTuple2._1, Collections.emptySet(), userTuple2._2.getAttributes()),
                        permissions -> map(userTuple2._1, permissions, userTuple2._2.getAttributes())
                );
    }

    private OAuth2UserPrincipal map(User user, Set<Permission> permissions, Map<String, Object> attributes) {
        return new OAuth2UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getIsExpired(),
                user.getIsLocked(),
                user.getIsCredentialsExpired(),
                user.getIsEnabled(),
                permissions.stream().map(Permission::getName).map(SimpleGrantedAuthority::new).toList(),
                attributes
        );
    }
}
