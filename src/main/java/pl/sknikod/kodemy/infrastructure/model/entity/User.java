package pl.sknikod.kodemy.infrastructure.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.util.Auditable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends Auditable<String> {
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
    @OneToMany(mappedBy = "user", cascade = CascadeType.PERSIST)
    private Set<Provider> providers = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    private Role role;
    @OneToMany(mappedBy = "user")
    private Set<Grade> grades = new HashSet<>();
    @OneToMany(mappedBy = "user")
    private Set<Material> material = new HashSet<>();

    public User(String username, String email, String photo, Role role) {
        this.username = username;
        this.email = email;
        this.photo = photo;
        this.role = role;
        isExpired = isLocked = isCredentialsExpired = false;
        isEnabled = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(username, user.username) && Objects.equals(email, user.email) && Objects.equals(photo, user.photo) && Objects.equals(isExpired, user.isExpired) && Objects.equals(isLocked, user.isLocked) && Objects.equals(isCredentialsExpired, user.isCredentialsExpired) && Objects.equals(isEnabled, user.isEnabled);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, photo, isExpired, isLocked, isCredentialsExpired, isEnabled);
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
                "} " + super.toString();
    }
}