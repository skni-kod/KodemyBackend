package pl.sknikod.kodemy.infrastructure.model.user;

import io.vavr.control.Option;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sknikod.kodemy.infrastructure.model.role.Role;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

    private final Long id;
    private final String password = null;
    private final String username;
    private final String email;
    private final Role role;
    private final Boolean isExpired;
    private final Boolean isLocked;
    private final Boolean isCredentialsExpired;
    private final Boolean isEnabled;
    @Setter
    private Set<SimpleGrantedAuthority> authorities;
    private final Map<String, Object> attributes;

    public UserPrincipal(Long id, String username, String email, Role role, Boolean isExpired, Boolean isLocked, Boolean isCredentialsExpired, Boolean isEnabled, Set<SimpleGrantedAuthority> authorities, Map<String, Object> attributes) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.isExpired = isExpired;
        this.isLocked = isLocked;
        this.isCredentialsExpired = isCredentialsExpired;
        this.isEnabled = isEnabled;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public static UserPrincipal create(User user, Set<SimpleGrantedAuthority> authorities, Map<String, Object> attributes) {
        return new UserPrincipal(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole(),
                user.getIsExpired(), user.getIsLocked(), user.getIsCredentialsExpired(), user.getIsEnabled(),
                authorities, attributes
        );
    }

    public static boolean checkPrivilege(String privilege) {
        return Option.of(getCurrentSessionUser())
                .map(UserPrincipal::getAuthorities)
                .map(HashSet::new)
                .map(perms -> perms.contains(new SimpleGrantedAuthority(privilege)))
                .getOrElse(false);
    }

    public static UserPrincipal getCurrentSessionUser() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(auth -> (UserPrincipal) auth.getPrincipal())
                .orElse(null);
    }

    @Override
    public boolean isAccountNonExpired() {
        return !isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }
}