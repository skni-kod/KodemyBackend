package pl.sknikod.kodemybackend.infrastructure.common.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemybackend.configuration.SecurityConfig;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "authors")
public class Author {
    @Id
    @Column(nullable = false, unique = true)
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;

    public static Author map(SecurityConfig.UserPrincipal userDetails) {
        return new Author(userDetails.getId(), userDetails.getUsername());
    }
}
