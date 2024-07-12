package pl.sknikod.kodemyauth.infrastructure.database.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;
import pl.sknikod.kodemyauth.infrastructure.module.user.model.SimpleUserResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query(
            value = """
                    SELECT u FROM User u \
                    INNER JOIN Provider up ON u = up.user \
                    LEFT JOIN FETCH u.role \
                    WHERE up.principalId = :principalId AND up.providerType = :registrationId\
                    """
    )
    User findUserByPrincipalIdAndAuthProviderWithFetchRole(String principalId, String registrationId);

    @Query(
            value = """
                    select u from User u \
                    where u.role.name in (:roles) and u.isEnabled = true and u.isLocked = false \
                    and u.isCredentialsExpired = false and u.isExpired = false\
                    """
    )
    HashSet<User> findUsersByRoleAdmin(Set<Role.RoleName> roles);

    ArrayList<User> findByUsernameContainingOrEmailContaining(String username, String email);

    @Query("""
            SELECT u FROM User u \
            WHERE (:username IS NULL OR u.username = :username) \
            AND (:email IS NULL OR u.email = :email) \
            AND (:role IS NULL OR u.role = :role)\
            """)
    Page<User> findByUsernameOrEmailOrRole(
            String username,
            String email,
            Role role,
            Pageable pageable
    );

    @Query("SELECT u.id, u.username FROM User u WHERE u.id IN :ids")
    List<Object[]> findAllByIds(Set<Long> ids);
}