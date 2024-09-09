package pl.sknikod.kodemybackend.factory;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sknikod.kodemycommon.security.UserPrincipal;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class UserFactory {
    private UserFactory() {
    }

    public static UserPrincipal userPrincipal = userPrincipal(Collections.emptySet());

    public static UserPrincipal userPrincipal(Set<SimpleGrantedAuthority> authorities) {
        return new UserPrincipal(
                1L, "username",
                false, false, false, true,
                authorities
        );
    }

}
