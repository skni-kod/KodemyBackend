package pl.sknikod.kodemy.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = "SELECT u FROM User u INNER JOIN Provider up ON u = up.user WHERE up.principalId = :principalId AND up.providerType = :providerType"
    )
    User findUserByPrincipalIdAndAuthProvider(String principalId, UserProviderType providerType);

    @Query(
            value = "select r.users from Role r where r.name in ('ROLE_ADMIN', 'ROLE_SUPERADMIN')"
    )
    HashSet<User> findUsersByRoleAdmin();
}