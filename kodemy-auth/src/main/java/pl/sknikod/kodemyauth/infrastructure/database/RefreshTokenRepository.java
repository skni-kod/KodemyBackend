package pl.sknikod.kodemyauth.infrastructure.database;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    @Query(
            value = """
                    SELECT t FROM RefreshToken t \
                    LEFT JOIN FETCH t.user \
                    WHERE t.token = :token
                    """
    )
    RefreshToken findRTByTokenWithFetchUser(UUID token);

    @Modifying
    @Query(
            value = """
                    DELETE RefreshToken t \
                    WHERE t.user.id = :userId AND t.bearerId = :bearerJti
                    """
    )
    void deleteRTByUserIdAndBearerJti(Long userId, UUID bearerJti);

    Optional<RefreshToken> findByTokenAndExpiredDateAfter(UUID token, LocalDateTime now);
}