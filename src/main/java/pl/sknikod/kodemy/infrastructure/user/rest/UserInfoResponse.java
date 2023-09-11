package pl.sknikod.kodemy.infrastructure.user.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import java.util.Date;

@Value
public class UserInfoResponse {
    Long id;
    String username;
    String email;
    String photo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-ddTHH:mm:ss")
    Date createdDate;
    RoleDetails role;

    @Value
    public static class RoleDetails {
        Long id;
        String name;
    }
}
