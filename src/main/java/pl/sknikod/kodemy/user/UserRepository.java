package pl.sknikod.kodemy.user;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    @Query(value = "SELECT u.* FROM users u INNER JOIN users_provider up ON u.id = up.user_id WHERE up.principal_id LIKE ?1 AND up.provider_type LIKE ?2", nativeQuery = true)
    User findUserByPrincipalIdAndAuthProvider(String principalId, UserProviderType providerType);
}
