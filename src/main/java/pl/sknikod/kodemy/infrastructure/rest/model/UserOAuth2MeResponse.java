package pl.sknikod.kodemy.infrastructure.rest.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.util.Date;

@Value
public class UserOAuth2MeResponse {
    Long id;
    String username;
    String email;
    String photo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Europe/Warsaw")
    Date createdDate;
    RoleDetails role;

    @Value
    public static class RoleDetails {
        Long id;
        String name;
    }
}
