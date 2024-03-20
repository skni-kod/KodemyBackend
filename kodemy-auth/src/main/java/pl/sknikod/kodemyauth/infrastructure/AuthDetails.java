package pl.sknikod.kodemyauth.infrastructure;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Getter
@Setter
public class AuthDetails {
    private String accessToken;
    private String refreshToken;
    @NotNull private Map<String, @NotNull Object> extras = new HashMap<>();
}
