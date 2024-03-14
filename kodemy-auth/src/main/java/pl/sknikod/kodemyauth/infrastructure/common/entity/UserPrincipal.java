package pl.sknikod.kodemyauth.infrastructure.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password = null;
    private final String email;
    private final transient Role role;
    private final AccountStatus status;
    private final Set<SimpleGrantedAuthority> authorities;

    private UserPrincipal(Long id, String username, String email, Role role, AccountStatus status, Set<SimpleGrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user, Set<SimpleGrantedAuthority> authorities) {
        return new UserPrincipal(
                user.getId(), user.getUsername(), user.getEmail(), user.getRole(),
                new AccountStatus(
                        user.getIsExpired(), user.getIsLocked(), user.getIsCredentialsExpired(), user.getIsEnabled()
                ),
                authorities
        );
    }

    public static UserPrincipal create(UserPrincipal userPrincipal, Set<SimpleGrantedAuthority> authorities) {
        return new UserPrincipal(
                userPrincipal.getId(), userPrincipal.getUsername(), userPrincipal.email, userPrincipal.getRole(),
                new AccountStatus(
                        userPrincipal.status.isExpired, userPrincipal.status.isLocked,
                        userPrincipal.status.isCredentialsExpired, userPrincipal.status.isEnabled),
                authorities
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

    @Getter
    @AllArgsConstructor
    private static class AccountStatus implements Serializable {
        private final Boolean isExpired;
        private final Boolean isLocked;
        private final Boolean isCredentialsExpired;
        private final Boolean isEnabled;
    }
}