package pl.sknikod.kodemy.rest.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Value;
import pl.sknikod.kodemy.rest.BaseDetails;

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
    @EqualsAndHashCode(callSuper = true)
    public static class RoleDetails extends BaseDetails {
        public RoleDetails(Long id, String name){
            super(id,name);
        }
    }
}