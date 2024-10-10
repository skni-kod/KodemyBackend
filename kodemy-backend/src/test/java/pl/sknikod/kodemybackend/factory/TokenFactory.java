package pl.sknikod.kodemybackend.factory;

import pl.sknikod.kodemycommons.security.JwtProvider;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

public class TokenFactory {
    private TokenFactory() {}

    public static JwtProvider.Token jwtServiceToken = jwtServiceToken();
    public static JwtProvider.Token.Deserialize jwtServiceTokenDeserialize = jwtServiceTokenDeserialize();

    private static JwtProvider.Token jwtServiceToken() {
        return new JwtProvider.Token(
                UUID.randomUUID(),
                "header.payload.signature",
                new Date()
        );
    }

    private static JwtProvider.Token.Deserialize jwtServiceTokenDeserialize() {
        return new JwtProvider.Token.Deserialize(
                1L,
                "username",
                1,
                Collections.emptySet()
        );
    }
}
