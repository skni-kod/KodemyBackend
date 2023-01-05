package pl.sknikod.kodemy.user;

import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.role.Role;
import pl.sknikod.kodemy.role.RoleName;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String principalId;
    private String name;
    @Email
    private String email;
    private String photo;
    private LocalDateTime created;
    private LocalDateTime lastLogin;
    @Enumerated(EnumType.STRING)
    private UserProvider userProvider;

    @ManyToMany
    @JoinTable(
            name = "users_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<Grade> grades = new HashSet<>();

    @OneToOne(mappedBy = "user")
    private Material material;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public UserProvider getUserProvider() {
        return userProvider;
    }

    public void setUserProvider(UserProvider userProvider) {
        this.userProvider = userProvider;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id) && principalId.equals(user.principalId) && name.equals(user.name) && email.equals(user.email) && created.equals(user.created);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, principalId, name, email, created);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", principalId='" + principalId + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", created=" + created +
                ", lastLogin=" + lastLogin +
                ", userProvider=" + userProvider +
                ", roles=" + roles +
                '}';
    }

    public boolean addRole(Role role) {
        return roles.add(role);
    }

    public boolean removeRole(Role role) {
        if (role.getName().equals(RoleName.USER)) return false;
        return roles.remove(role);
    }
}