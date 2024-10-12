package pl.sknikod.kodemyauth.infrastructure.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemycommons.data.BaseEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String username;
    @Email
    private String email;
    private String photo;
    @Column(nullable = false)
    private Boolean isExpired;
    @Column(nullable = false)
    private Boolean isLocked;
    @Column(nullable = false)
    private Boolean isCredentialsExpired;
    @Column(nullable = false)
    private Boolean isEnabled;
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<Provider> providers = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public User(String username, String email, String photo, Role role) {
        this.username = username;
        this.email = email;
        this.photo = photo;
        this.role = role;
        isExpired = isLocked = isCredentialsExpired = false;
        isEnabled = true;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof User user)) return false;
        if (!super.equals(object)) return false;
        return Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(isExpired, user.isExpired) && Objects.equals(isLocked, user.isLocked) && Objects.equals(isCredentialsExpired, user.isCredentialsExpired) && Objects.equals(isEnabled, user.isEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, email, isExpired, isLocked, isCredentialsExpired, isEnabled);
    }
}