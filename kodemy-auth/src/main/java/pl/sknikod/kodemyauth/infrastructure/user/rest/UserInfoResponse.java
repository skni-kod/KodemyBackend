package pl.sknikod.kodemyauth.infrastructure.user.rest;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public record UserInfoResponse(
        Long id,
        String username,
        String email,
        String photo,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") Date createdDate,
        UserInfoResponse.RoleDetails role,
        Boolean isEnabled,
        Boolean isLocked
) {
    public record RoleDetails(Long id, String name) {
    }
}
