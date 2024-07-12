package pl.sknikod.kodemyauth.infrastructure.database.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.sknikod.kodemyauth.infrastructure.database.entity.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.entity.User;

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
}