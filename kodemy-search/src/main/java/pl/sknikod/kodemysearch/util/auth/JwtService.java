package pl.sknikod.kodemysearch.util.auth;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.vavr.control.Try;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import pl.sknikod.kodemysearch.exception.structure.AuthenticationException;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtService {
    private final JwtProperties jwtProperties;
    private final SecretKey key;
    private static final String ISSUER = "pl.sknikod.kodemy";
    private final JwtParser parser;

    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.key = generateKey(jwtProperties.secretKey);
        this.parser = buildParser(this.key);
    }

    private JwtParser buildParser(SecretKey key) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(ISSUER)
                .build();
    }

    private SecretKey generateKey(String secretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Token generateDelegationToken(String subject, String authority) {
        Assert.notNull(subject, "input cannot be null");
        Assert.notNull(authority, "authority cannot be null");
        return generateToken(TokenType.DELEGATION_TOKEN, subject, Map.of(
                ClaimKey.AUTHORITIES, List.of(authority)));
    }

    public Token generateUserToken(@NonNull Input input) {
        Assert.notNull(input, "input cannot be null");
        var authorities = input.authorities != null && !input.authorities.isEmpty()
                ? input.authorities : Collections.emptySet();
        return generateToken(TokenType.USER_TOKEN, input.username, Map.of(
                ClaimKey.ID, input.id,
                ClaimKey.STATE, input.state,
                ClaimKey.AUTHORITIES, authorities));
    }

    public Token generateToken(TokenType tokenType, String subject, Map<String, ?> claims) {
        Assert.notNull(tokenType, "tokenType cannot be null");
        Assert.notNull(subject, "subject cannot be null");
        Assert.notNull(claims, "claims cannot be null");

        final UUID jti = UUID.randomUUID();
        final Date issuedAt = new Date(System.currentTimeMillis());
        final Date expiration = new Date(issuedAt.getTime() + 1000L * 60 * switch (tokenType) {
            case USER_TOKEN -> jwtProperties.bearer.expirationMin;
            case DELEGATION_TOKEN -> jwtProperties.delegation.expirationMin;
        });
        return new Token(jti, generate(jti, subject, issuedAt, expiration, claims), expiration);

    }

    private String generate(
            UUID jti,
            String subject,
            Date issuedAt,
            Date expiration,
            Map<String, ?> claims
    ) {
        final var jwt = Jwts.builder()
                .issuer(ISSUER)
                .id(jti.toString())
                .subject(subject)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(key)
                .claims(claims);
        return jwt.compact();
    }

    public Try<Token.Deserialize> parseToken(String token) {
        return parseClaims(token)
                .mapTry(claims -> {
                    var id = claims.get(ClaimKey.ID, Long.class);
                    var username = claims.get(ClaimKey.USERNAME, String.class);
                    @SuppressWarnings("unchecked")
                    List<String> roles = (List<String>) claims.get(ClaimKey.AUTHORITIES, List.class);
                    var authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toSet());
                    var state = Optional.ofNullable(claims.get(ClaimKey.STATE, Integer.class)).orElse(8);
                    return new Token.Deserialize(id, username, state, authorities);
                })
                .onFailure(th -> log.error("Cannot parse token"));
    }

    private Try<Claims> parseClaims(String token) {
        return Try.of(() -> parser.parseSignedClaims(token))
                .mapTry(Jwt::getPayload)
                .recoverWith(ExpiredJwtException.class, th ->
                        Try.failure(new AuthenticationException("JWT expired")))
                .recoverWith(SignatureException.class, th ->
                        Try.failure(new AuthenticationException("JWT signature is invalid")));
    }

    public enum TokenType {
        USER_TOKEN,
        DELEGATION_TOKEN
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    private static class ClaimKey {
        static String ID = "id";
        static String USERNAME = Claims.SUBJECT;
        static String STATE = "state";
        static String AUTHORITIES = "authorities";
    }

    @Getter
    @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
    public static class Input {
        Long id;
        String username;
        Integer state;
        Set<SimpleGrantedAuthority> authorities;

        public Input(
                @NonNull Long id,
                @NonNull String username,
                boolean isExpired, boolean isLocked, boolean isCredentialsExpired, boolean isEnabled,
                @NonNull Set<SimpleGrantedAuthority> authorities
        ) {
            Assert.notNull(id, "id cannot be null");
            Assert.notNull(username, "username cannot be null");
            Assert.notNull(authorities, "authorities cannot be null");

            this.id = id;
            this.username = username;
            this.state = mapToState(isExpired, isLocked, isCredentialsExpired, isEnabled);
            this.authorities = authorities;
        }

        private int mapToState(Boolean isExpired, Boolean isLocked, Boolean isCredentialsExpired, Boolean isEnabled) {
            int state = 0;
            if (isExpired) state |= 1;
            if (isLocked) state |= 2;
            if (isCredentialsExpired) state |= 4;
            if (isEnabled) state |= 8;
            return state;
        }
    }

    public record Token(UUID id, String value, Date expiration) {
        @Getter
        @FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
        public static class Deserialize {
            Long id;
            String username;
            boolean isExpired;
            boolean isLocked;
            boolean isCredentialsExpired;
            boolean isEnabled;
            Set<SimpleGrantedAuthority> authorities;

            public Deserialize(Long id, String username, Integer state, Set<SimpleGrantedAuthority> authorities) {
                this.id = id;
                this.username = username;
                if (state != null) {
                    this.isExpired = (state & 1) != 0;
                    this.isLocked = (state & 2) != 0;
                    this.isCredentialsExpired = (state & 4) != 0;
                    this.isEnabled = (state & 8) != 0;
                } else {
                    this.isExpired = this.isLocked = this.isCredentialsExpired = false;
                    this.isEnabled = true;
                }
                this.authorities = authorities;
            }
        }
    }

    @Getter
    @Setter
    @Component
    @NoArgsConstructor
    @ConfigurationProperties(prefix = "jwt")
    public static class JwtProperties {
        private String secretKey;
        private Bearer bearer = new Bearer();
        private Delegation delegation = new Delegation();

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Bearer {
            private Integer expirationMin = 15;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Delegation {
            private Integer expirationMin = 60;
        }

    }
}
