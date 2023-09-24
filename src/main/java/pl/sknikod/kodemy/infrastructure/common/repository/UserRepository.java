package pl.sknikod.kodemy.infrastructure.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemy.infrastructure.common.entity.RoleName;
import pl.sknikod.kodemy.infrastructure.common.entity.User;
import pl.sknikod.kodemy.infrastructure.common.entity.UserProviderType;

import java.util.ArrayList;
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
    HashSet<User> findUsersByRoleAdmin(Set<RoleName> roles);

    ArrayList<User> findByUsernameContainingOrEmailContaining(String userName, String email);
}