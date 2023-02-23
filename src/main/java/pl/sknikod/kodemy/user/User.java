package pl.sknikod.kodemy.user;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.role.Role;
import pl.sknikod.kodemy.role.RoleName;
import pl.sknikod.kodemy.provider.Provider;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails, OAuth2User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String username;
    @Email
    private String email;
    private String photo;
    private Boolean isExpired;
    private Boolean isLocked;
    private Boolean isCredentialsExpired;
    private Boolean isEnabled;
    @Transient
    private Map<String, Object> attributes;
    @OneToMany(mappedBy = "user", cascade = {
            CascadeType.PERSIST
    })
    private Set<Provider> providers = new HashSet<>();
    @ManyToMany(cascade = {
            CascadeType.PERSIST
    })
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    @OneToMany(mappedBy = "user")
    private Set<Grade> grades = new HashSet<>();
    @OneToOne(mappedBy = "user")
    private Material material;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private LocalDateTime lastLoginDate;

    private User(UserBuilder builder) {
        username = builder.username;
        email = builder.email;
        photo = builder.photo;
        isExpired = builder.isExpired;
        isLocked = builder.isLocked;
        isCredentialsExpired = builder.isCredentialsExpired;
        isEnabled = builder.isEnabled;
        attributes = builder.attributes;
        providers = builder.providers;
        roles = builder.roles;
        createdDate = builder.createdDate;
        lastModifiedDate = builder.lastModifiedDate;
        lastLoginDate = builder.lastLoginDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(photo, user.photo) && Objects.equals(isExpired, user.isExpired) && Objects.equals(isLocked, user.isLocked) && Objects.equals(isCredentialsExpired, user.isCredentialsExpired) && Objects.equals(isEnabled, user.isEnabled) && Objects.equals(createdDate, user.createdDate) && Objects.equals(lastModifiedDate, user.lastModifiedDate) && Objects.equals(lastLoginDate, user.lastLoginDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, photo, isExpired, isLocked, isCredentialsExpired, isEnabled, createdDate, lastModifiedDate, lastLoginDate);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", isExpired=" + isExpired +
                ", isLocked=" + isLocked +
                ", isCredentialsExpired=" + isCredentialsExpired +
                ", isEnabled=" + isEnabled +
                ", createdDate=" + createdDate +
                ", lastModifiedDate=" + lastModifiedDate +
                ", lastLoginDate=" + lastLoginDate +
                '}';
    }

    public boolean addRole(Role role) {
        return roles.add(role);
    }

    public boolean removeRole(Role role) {
        if (role.getName().equals(RoleName.USER)) return false;
        return roles.remove(role);
    }

    @Override
    public String getName() {
        return username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName().toString()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return null;
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
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public static class UserBuilder {
        // required
        private String username;
        private String email;
        private String photo;
        private Map<String, Object> attributes;

        // default
        private Boolean isExpired;
        private Boolean isLocked;
        private Boolean isCredentialsExpired;
        private Boolean isEnabled;
        private Set<Provider> providers = new HashSet<>();
        private Set<Role> roles = new HashSet<>();
        private LocalDateTime createdDate;
        private LocalDateTime lastModifiedDate;
        private LocalDateTime lastLoginDate;

        public UserBuilder(String username, String email, String photo, Map<String, Object> attributes, String principalId, UserProviderType authProvider) {
            this.username = username;
            this.email = email;
            this.photo = photo;
            this.attributes = attributes;

            isExpired = isLocked = isCredentialsExpired = false;
            isEnabled = true;
            createdDate = lastModifiedDate = lastLoginDate = null;

            Provider provider = new Provider();
            provider.setPrincipalId(principalId);
            provider.setProviderType(authProvider);
            provider.setEmail(email);
            provider.setPhoto(photo);
            providers.add(provider);

            Role role = new Role(RoleName.USER);
            roles.add(role);
        }

        public User build(){
            return new User(this);
        }
    }
}