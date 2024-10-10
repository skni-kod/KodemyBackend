package pl.sknikod.kodemyauth.infrastructure.database;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Long> {
    @Query(
            value = """
                    SELECT t FROM RefreshToken t \
                    LEFT JOIN FETCH t.user \
                    WHERE t.token = :token AND t.bearerId = :bearerJti \
                    """
    )
    RefreshToken findRTByTokenAndBearerJtiWithFetchUser(UUID token, UUID bearerJti);

    @Modifying
    @Query(
            value = """
                    DELETE RefreshToken t \
                    WHERE t.user.id = :userId AND t.bearerId = :bearerJti
                    """
    )
    void deleteRTByUserIdAndBearerJti(Long userId, UUID bearerJti);
}