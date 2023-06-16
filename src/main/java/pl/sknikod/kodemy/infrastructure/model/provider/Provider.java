package pl.sknikod.kodemy.infrastructure.model.provider;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.infrastructure.model.user.User;
import pl.sknikod.kodemy.infrastructure.model.user.UserProviderType;
import pl.sknikod.kodemy.util.Auditable;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "providers")
public class Provider extends Auditable<String> {
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

    public Provider(String principalId, UserProviderType providerType, String email, String photo, User user) {
        this.principalId = principalId;
        this.providerType = providerType;
        this.email = email;
        this.photo = photo;
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Provider that = (Provider) o;
        return Objects.equals(id, that.id) && Objects.equals(principalId, that.principalId) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, principalId, user);
    }

    @Override
    public String toString() {
        return "Provider{" +
                "id=" + id +
                ", principalId='" + principalId + '\'' +
                ", providerType=" + providerType +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", user=" + user +
                "} " + super.toString();
    }
}