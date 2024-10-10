package pl.sknikod.kodemyauth.infrastructure.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemycommons.data.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken extends BaseEntity {
    @Column(nullable = false, updatable = false)
    private UUID token;
    @Column(nullable = false, updatable = false)
    private UUID bearerId;
    @Column(nullable = false)
    private LocalDateTime expiredDate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    public RefreshToken(UUID token, UUID bearerId, LocalDateTime expiredDate, User user) {
        this.token = token;
        this.bearerId = bearerId;
        this.expiredDate = expiredDate;
        this.user = user;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof RefreshToken that)) return false;
        if (!super.equals(object)) return false;
        return Objects.equals(token, that.token) && Objects.equals(bearerId, that.bearerId) && Objects.equals(expiredDate, that.expiredDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), token, bearerId, expiredDate);
    }
}