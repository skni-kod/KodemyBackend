package pl.sknikod.kodemyauth.infrastructure.common.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType type;

    @Column(nullable = false)
    private Instant issuedAt;

    @Column(nullable = false)
    private Instant expiration;

    @Column(nullable = false)
    private boolean isRevoked;

    @Column(nullable = false)
    private Long userId;

    public enum TokenType {
        ACCESS_TOKEN,
        REFRESH_TOKEN
    }

    public Token(String token, TokenType type, Instant issuedAt, Instant expiration, Long userId) {
        this.token = token;
        this.type = type;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
        this.userId = userId;
        this.isRevoked = false;
    }
}