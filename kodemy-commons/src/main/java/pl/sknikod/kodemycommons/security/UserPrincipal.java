package pl.sknikod.kodemycommons.security;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class UserPrincipal implements UserDetails, Serializable {
    @Serial
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
    @Getter
    Long id;
    @Getter
    String username;
    Boolean isExpired;
    Boolean isLocked;
    Boolean isCredentialsExpired;
    Boolean isEnabled;
    @Getter
    Set<SimpleGrantedAuthority> authorities;

    public UserPrincipal(
            Long id,
            String username,
            Boolean isExpired,
            Boolean isLocked,
            Boolean isCredentialsExpired,
            Boolean isEnabled,
            Collection<SimpleGrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.isExpired = isExpired;
        this.isLocked = isLocked;
        this.isCredentialsExpired = isCredentialsExpired;
        this.isEnabled = isEnabled;
        this.authorities = Collections.unmodifiableSet(new LinkedHashSet<>(
                (authorities != null) ? authorities : Collections.emptyList()
        ));
    }

    public UserPrincipal(
            Long id,
            String username,
            Collection<SimpleGrantedAuthority> authorities
    ) {
        this(id, username, false, false, false, true, authorities);
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public boolean isAccountNonExpired() {
        return !this.isExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !this.isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return !this.isCredentialsExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.isEnabled;
    }
}