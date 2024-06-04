package pl.sknikod.kodemyauth.factory;

import pl.sknikod.kodemyauth.infrastructure.database.entity.RefreshToken;
import pl.sknikod.kodemyauth.infrastructure.database.entity.Role;
import pl.sknikod.kodemyauth.util.auth.JwtService;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class TokenFactory {
    private TokenFactory() {}

    public static RefreshToken refreshToken = refreshToken();
    public static JwtService.Token jwtServiceToken = jwtServiceToken();
    public static JwtService.Token.Deserialize jwtServiceTokenDeserialize = jwtServiceTokenDeserialize();

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

    private static JwtService.Token jwtServiceToken() {
        return new JwtService.Token(
                UUID.randomUUID(),
                "header.payload.signature",
                new Date()
        );
    }

    private static JwtService.Token.Deserialize jwtServiceTokenDeserialize() {
        return new JwtService.Token.Deserialize(
                1L,
                "username",
                1,
                Collections.emptySet()
        );
    }
}
