package pl.sknikod.kodemybackend.factory;

import pl.sknikod.kodemybackend.util.auth.JwtService;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class TokenFactory {
    private TokenFactory() {}

    public static JwtService.Token jwtServiceToken = jwtServiceToken();
    public static JwtService.Token.Deserialize jwtServiceTokenDeserialize = jwtServiceTokenDeserialize();

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
