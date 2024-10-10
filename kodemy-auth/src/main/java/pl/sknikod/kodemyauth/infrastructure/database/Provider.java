package pl.sknikod.kodemyauth.infrastructure.database;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemycommons.data.BaseEntity;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "providers")
public class Provider extends BaseEntity {
    @Column(nullable = false)
    private String principalId;
    private String providerType;
    @Email
    private String email;
    private String photo;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Provider(String principalId, String providerType, String email, String photo, User user) {
        this.principalId = principalId;
        this.providerType = providerType;
        this.email = email;
        this.photo = photo;
        this.user = user;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Provider provider)) return false;
        if (!super.equals(object)) return false;
        return Objects.equals(principalId, provider.principalId) && Objects.equals(providerType, provider.providerType) && Objects.equals(email, provider.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), principalId, providerType, email);
    }
}