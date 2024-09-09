package pl.sknikod.kodemyauth.infrastructure.database.view;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserAggregateViewRepository extends JpaRepository<UserAggregateView, Long> {
    Page<UserAggregateView> findByUsernameOrEmailOrRoleName(
            String username,
            String email,
            String roleName,
            Pageable pageable
    );
}