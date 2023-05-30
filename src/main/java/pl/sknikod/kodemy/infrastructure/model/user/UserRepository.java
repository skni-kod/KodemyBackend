package pl.sknikod.kodemy.infrastructure.model.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = "SELECT u FROM User u INNER JOIN Provider up ON u = up.user WHERE up.principalId = :principalId AND up.providerType = :providerType"
    )
    User findUserByPrincipalIdAndAuthProvider(String principalId, UserProviderType providerType);

    @Query(
            value = "select u from User u where u.role.name in (:roles) and u.isEnabled = true and u.isLocked = false and u.isCredentialsExpired = false and u.isExpired = false"
    )
    HashSet<User> findUsersByRoleAdmin(Set<String> roles);
}