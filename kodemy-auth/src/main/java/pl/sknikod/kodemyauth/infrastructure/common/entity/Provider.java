package pl.sknikod.kodemyauth.infrastructure.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private Provider.ProviderType providerType;
    @Email
    private String email;
    private String photo;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Provider(String principalId, Provider.ProviderType providerType, String email, String photo, User user) {
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

    public enum ProviderType {
        GITHUB
    }
}