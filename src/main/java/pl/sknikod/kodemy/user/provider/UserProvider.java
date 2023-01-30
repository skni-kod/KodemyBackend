package pl.sknikod.kodemy.user.provider;

import pl.sknikod.kodemy.user.User;
import pl.sknikod.kodemy.user.UserProviderType;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.Objects;

@Entity
@Table(name = "users_provider")
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrincipalId() {
        return principalId;
    }

    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    public UserProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(UserProviderType providerType) {
        this.providerType = providerType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

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