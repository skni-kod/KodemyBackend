package pl.sknikod.kodemybackend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Component
public class JwtUtil {
    @Value("${kodemy.jwt.expiration-mins:15}")
    private int expirationMins;
    @Value("${kodemy.jwt.secret-key}")
    private String secretKey;

    public Output generateToken(Input input) {
        final var authorities = input.getAuthorities()
                .stream()
                .map(SimpleGrantedAuthority::getAuthority)
                .toList();
        final var claims = Map.of(
                "id", input.id,
                "username", input.username,
                "authorities", authorities
        );
        return createToken(input.username, claims);
    }

    private Output createToken(String subject, Map<String, ?> claims) {
        final Date createdDate = new Date(System.currentTimeMillis());
        final Date expirationDate = new Date(createdDate.getTime() + 1000L * 60 * expirationMins);
        String bearer = Jwts.builder()
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
        return new Output(bearer);
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        return claimResolver.apply(getClaimsJws(token).getBody());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public boolean isTokenValid(String token) {
        var nowDate = new Date(System.currentTimeMillis());
        try {
            Jws<Claims> claimsJws = getClaimsJws(token);
            return !claimsJws.getBody().getExpiration().before(nowDate);
        } catch (Exception e) {
            return false;
        }
    }

    private Jws<Claims> getClaimsJws(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token);
    }

    public Output.Deserialize deserializeToken(String token) {
        Claims claims = getClaimsJws(token).getBody();
        return new Output.Deserialize(
                claims.get("id", Long.class),
                claims.get("username", String.class),
                claims.get("authorities", List.class)
        );
    }

    @Data
    @AllArgsConstructor
    public static class Input {
        private Long id;
        private String username;
        private Set<SimpleGrantedAuthority> authorities;
    }

    @lombok.Value
    public static class Output {
        String bearer;

        @lombok.Value
        public static class Deserialize {
            Long id;
            String username;
            List<String> authorities;
        }
    }
}
