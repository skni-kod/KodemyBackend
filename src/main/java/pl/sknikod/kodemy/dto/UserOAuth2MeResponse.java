package pl.sknikod.kodemy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.Date;
import java.util.Set;

@Value
public class UserOAuth2MeResponse {
    Long id;
    String username;
    String email;
    String photo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssZ", timezone = "Europe/Warsaw")
    Date createdDate;
    Set<RoleDetails> roles;

    @Value
    @EqualsAndHashCode(callSuper = true)
    public static class RoleDetails extends BaseDetails {
        public RoleDetails(Long id, String name){
            super(id,name);
        }
    }
}
