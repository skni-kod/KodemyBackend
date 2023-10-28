package pl.sknikod.kodemyauth.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Provider;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.common.entity.User;

import java.util.ArrayList;
import java.util.HashSet;
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

    @Query(
            value = "SELECT DISTINCT u FROM User u " +
                    "LEFT JOIN FETCH u.role " +
                    "WHERE u.username LIKE %:userName% OR u.email LIKE %:email%"
    )
    ArrayList<User> findByUsernameContainingOrEmailContainingWithFetchRole(String userName, String email);
}