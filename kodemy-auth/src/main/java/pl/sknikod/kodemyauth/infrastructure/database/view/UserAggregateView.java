package pl.sknikod.kodemyauth.infrastructure.database.view;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "users_aggregate")
public class UserAggregateView {

    @Id
    private Long id;
    private String username;
    private String email;
    private String photo;
    private Date createdDate;
    private Boolean isActive;
    private Boolean isLocked;
    private Long roleId;
    private String roleName;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof UserAggregateView that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(username, that.username) && Objects.equals(email, that.email) && Objects.equals(roleId, that.roleId) && Objects.equals(roleName, that.roleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username, email, roleId, roleName);
    }
}
