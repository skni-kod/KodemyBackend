package pl.sknikod.kodemyauth.util;

import com.nimbusds.jwt.JWT;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.security.Key;
import java.util.*;
import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtil {

    public static Output generateToken(Input input, @NotNull @NotEmpty String secretKey, int expirationInMins) {
        return createToken(input.subject, input.extraClaims, secretKey, expirationInMins);
    }

    private static Output createToken(String subject, Map<String, ?> claims, String secretKey, int expirationInMins) {
        final Date createdDate = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(createdDate.getTime() + 1000L * 60 * expirationInMins);
        String token = Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(getSignKey(secretKey), SignatureAlgorithm.HS256)
                .compact();
        return new Output(token, createdDate, expirationDate);
    }

    private static Key getSignKey(String secretKey) {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String extractUsername(String token, String secretKey) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver, String secretKey) {
        return claimResolver.apply(getClaimsJws(token, secretKey).getBody());
    }

    public Date extractExpiration(String token, String secretKey) {
        return extractClaim(token, Claims::getExpiration, secretKey);
    }

    public boolean isTokenValid(String token, String secretKey) {
        return isTokenValid(token, null, secretKey);
    }

    public boolean isTokenValid(String token, @Nullable String username, String secretKey) {
        try {
            var nowDate = new Date(System.currentTimeMillis());
            var isExpired = extractExpiration(token, secretKey).before(nowDate);
            return (username != null) ? extractUsername(token, secretKey).equals(username) && !isExpired : !isExpired;
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> getClaimsJws(String token, String secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey(secretKey))
                .build()
                .parseClaimsJws(token);
    }

    @SuppressWarnings("unchecked")
    public Output.Deserialize deserialize(String token, String secretKey) {
        Claims claims = getClaimsJws(token, secretKey).getBody();
        return new Output.Deserialize(
                claims.get("subject", String.class),
                claims.get("iat", Date.class),
                claims.get("exp", Date.class),
                claims.get("extraClaims", Map.class)
        );
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        @NotNull private String subject;
        @NotNull private Map<String, @NotNull Object> extraClaims;
    }

    @lombok.Value
    public static class Output {
        @NotNull String value;
        @NotNull Date issuedAt;
        @NotNull Date expiration;

        @lombok.Value
        public static class Deserialize {
            @NotNull String subject;
            @NotNull Date issuedAt;
            @NotNull Date expiration;
            @NotNull Map<String, @NotNull Object> extraClaims;
        }
    }
}
