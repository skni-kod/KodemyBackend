package pl.sknikod.kodemyauth.infrastructure.module.oauth2.util;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sknikod.kodemyauth.util.auth.UserPrincipal;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class OAuth2UserPrincipal extends UserPrincipal implements OAuth2User {
    Map<String, Object> attributes;

    public OAuth2UserPrincipal(
            Long id,
            String username,
            Boolean isExpired,
            Boolean isLocked,
            Boolean isCredentialsExpired,
            Boolean isEnabled,
            Collection<SimpleGrantedAuthority> authorities,
            Map<String, Object> attributes) {
        super(id, username, isExpired, isLocked, isCredentialsExpired, isEnabled, authorities);
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(attributes));
    }

    @Override
    public String getName() {
        return this.username;
    }
}