package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.model.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.model.Role;
import pl.sknikod.kodemycommon.security.JwtProvider;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class TokenFactory {
    private TokenFactory() {}

    public static RefreshToken refreshToken = refreshToken();
    public static JwtProvider.Token jwtProviderToken = jwtProviderToken();
    public static JwtProvider.Token.Deserialize jwtProviderTokenDeserialize = jwtProviderTokenDeserialize();

    public static RefreshToken refreshToken() {
        var token = new RefreshToken(
                UUID.randomUUID(),
                null,
                null,
                UserFactory.user(RoleFactory.role(Role.RoleName.ROLE_USER))
        );
        token.setId(1L);
        return token;
    }

    private static JwtProvider.Token jwtProviderToken() {
        return new JwtProvider.Token(
                UUID.randomUUID(),
                "header.payload.signature",
                new Date()
        );
    }

    private static JwtProvider.Token.Deserialize jwtProviderTokenDeserialize() {
        return new JwtProvider.Token.Deserialize(
                1L,
                "username",
                1,
                Collections.emptySet()
        );
    }
}
