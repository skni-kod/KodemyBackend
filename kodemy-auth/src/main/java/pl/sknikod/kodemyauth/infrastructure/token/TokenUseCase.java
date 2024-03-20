package pl.sknikod.kodemyauth.infrastructure.token;

import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemyauth.configuration.SecurityConfig.SecurityProperties.AuthProperties;
import pl.sknikod.kodemyauth.infrastructure.common.entity.Token;
import pl.sknikod.kodemyauth.infrastructure.common.entity.UserPrincipal;
import pl.sknikod.kodemyauth.infrastructure.common.repository.TokenRepository;
import pl.sknikod.kodemyauth.infrastructure.common.repository.UserRepository;
import pl.sknikod.kodemyauth.infrastructure.common.rest.UserDetails;
import pl.sknikod.kodemyauth.util.JwtUtil;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class TokenUseCase {
    private final TokenRepository tokenRepository;
    private final AuthProperties authProperties;

    public Tokens generate(UserPrincipal userPrincipal) {
        AuthProperties.TokenProperties tokensProperties = authProperties.getToken();
        var authoritiesKey = "authorities";
        var input = new JwtUtil.Input(userPrincipal.getUsername(), new HashMap<>(Map.of(
                "id", userPrincipal.getId(),
                authoritiesKey, userPrincipal.getAuthorities()
                        .stream().map(SimpleGrantedAuthority::toString).toArray()
        )));
        var accessToken = JwtUtil.generateToken(input,
                tokensProperties.getAccess().getSecretKey(),
                tokensProperties.getAccess().getExpirationInMin()
        );
        saveToken(Token.TokenType.ACCESS_TOKEN, accessToken, userPrincipal.getId());;
        input.getExtraClaims().remove(authoritiesKey);
        var refreshToken = JwtUtil.generateToken(input,
                tokensProperties.getRefresh().getSecretKey(),
                tokensProperties.getRefresh().getExpirationInMin()
        );
        saveToken(Token.TokenType.REFRESH_TOKEN, refreshToken, userPrincipal.getId());
        return new Tokens(accessToken.getValue(), refreshToken.getValue());
    }

    private void saveToken(Token.TokenType tokenType, JwtUtil.Output token, Long id) {
        tokenRepository.save(new Token(
                token.getValue(),
                tokenType,
                token.getIssuedAt().toInstant(),
                token.getExpiration().toInstant(),
                id
        ));
    }

    @Value
    public static class Tokens {
        @NotNull String accessToken;
        @NotNull String refreshToken;
    }
}

