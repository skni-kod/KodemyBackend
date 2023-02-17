package pl.sknikod.kodemy.user.provider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.user.User;
import pl.sknikod.kodemy.user.UserProviderType;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "user_providers")
public class UserProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(nullable = false)
    private String principalId;
    @Enumerated(EnumType.STRING)
    private UserProviderType providerType;
    @Email
    private String email;
    private String photo;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserProvider that = (UserProvider) o;
        return Objects.equals(id, that.id) && Objects.equals(principalId, that.principalId) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, principalId, user);
    }

    @Override
    public String toString() {
        return "UserProvider{" +
                "id=" + id +
                ", principalId='" + principalId + '\'' +
                ", providerType=" + providerType +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}