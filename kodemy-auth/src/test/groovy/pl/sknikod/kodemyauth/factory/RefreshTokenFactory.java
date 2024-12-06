package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.RefreshToken;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshTokenFactory {
    public static RefreshToken create(UUID token, UUID bearerJti) {
        RefreshToken refreshToken = new RefreshToken(
                token,
                bearerJti,
                LocalDateTime.now(),
                UserFactory.create()
        );
        refreshToken.setId(1L);
        return refreshToken;
    }
}
