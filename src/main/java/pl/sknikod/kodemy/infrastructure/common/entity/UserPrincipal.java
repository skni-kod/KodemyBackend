package pl.sknikod.kodemy.infrastructure.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Getter
public class UserPrincipal implements UserDetails, OAuth2User {

    private final Long id;
    private final String password;
    private final String username;
    private final String email;
    private final transient Role role;
    private final AccountStatus status;
    private final Set<SimpleGrantedAuthority> authorities;
    private final transient Map<String, Object> attributes;

    private UserPrincipal(Long id, String username, String email, Role role, AccountStatus status, Set<SimpleGrantedAuthority> authorities, Map<String, Object> attributes) {
        this.id = id;
        this.username = username;
        this.password = "";
        this.email = email;
        this.role = role;
        this.status = status;
        this.authorities = authorities;
        this.attributes = attributes;
    }

    public static UserPrincipal create(User user, Set<SimpleGrantedAuthority> authorities, Map<String, Object> attributes) {
        return new UserPrincipal(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole(),
                new AccountStatus(user.getIsExpired(), user.getIsLocked(), user.getIsCredentialsExpired(), user.getIsEnabled()),
                authorities, attributes
        );
    }

    public static UserPrincipal create(UserPrincipal userPrincipal, Set<SimpleGrantedAuthority> authorities) {
        return new UserPrincipal(
                userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.email, userPrincipal.getRole(),
                new AccountStatus(
                        userPrincipal.status.isExpired, userPrincipal.status.isLocked,
                        userPrincipal.status.isCredentialsExpired, userPrincipal.status.isEnabled),
                authorities, userPrincipal.attributes
        );
    }

    @Override
    public boolean isAccountNonExpired() {
        return !status.isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !status.isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !status.isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return status.isEnabled;
    }

    @Override
    public String getName() {
        return String.valueOf(id);
    }

    @Getter
    @AllArgsConstructor
    private static class AccountStatus implements Serializable {
        private final Boolean isExpired;
        private final Boolean isLocked;
        private final Boolean isCredentialsExpired;
        private final Boolean isEnabled;
    }
}