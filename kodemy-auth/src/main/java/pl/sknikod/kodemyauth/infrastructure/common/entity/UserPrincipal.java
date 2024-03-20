package pl.sknikod.kodemyauth.infrastructure.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Null;
import java.io.Serializable;
import java.util.Set;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String email;
    private final PrincipalRole role;
    private final AccountStatus status;
    private final Set<SimpleGrantedAuthority> authorities;

    private UserPrincipal(
            Long id,
            String username,
            String email,
            PrincipalRole role,
            AccountStatus status,
            Set<SimpleGrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.status = status;
        this.authorities = authorities;
    }

    public static UserPrincipal create(User user, Set<SimpleGrantedAuthority> authorities) {
        return new UserPrincipal(
                user.getId(), user.getUsername(), user.getEmail(),
                new PrincipalRole(user.getRole().getId(), user.getRole().getName().name()),
                new AccountStatus(
                        user.getIsExpired(), user.getIsLocked(), user.getIsCredentialsExpired(), user.getIsEnabled()
                ),
                authorities
        );
    }

    @Override
    public @Null String getPassword() {
        return null;
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

    @AllArgsConstructor
    @Getter
    private static class PrincipalRole {
        Long id;
        String name;
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