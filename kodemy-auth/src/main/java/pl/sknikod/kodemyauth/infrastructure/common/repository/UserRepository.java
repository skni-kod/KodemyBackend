package pl.sknikod.kodemyauth.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.common.entity.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = "SELECT u FROM User u " +
                    "INNER JOIN Provider up ON u = up.user " +
                    "LEFT JOIN FETCH u.role " +
                    "WHERE up.principalId = :principalId AND up.providerType = :providerType"
    )
    User findUserByPrincipalIdAndAuthProviderWithFetchRole(String principalId, Provider.ProviderType providerType);

    @Query(
            value = "select u from User u " +
                    "where u.role.name in (:roles) and u.isEnabled = true and u.isLocked = false " +
                    "and u.isCredentialsExpired = false and u.isExpired = false"
    )
    HashSet<User> findUsersByRoleAdmin(Set<Role.RoleName> roles);

    ArrayList<User> findByUsernameContainingOrEmailContaining(String username, String email);

    @Query("SELECT u FROM User u " +
            "WHERE (:username IS NULL OR u.username = :username) " +
            "AND (:email IS NULL OR u.email = :email) " +
            "AND (:role IS NULL OR u.role = :role) ")
    List<User> findByUsernameOrEmailOrRole(
            @Param("username") String username,
            @Param("email") String email,
            @Param("role") Role role
    );
}