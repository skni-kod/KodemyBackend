package pl.sknikod.kodemy.rest.response;

import lombok.EqualsAndHashCode;
import lombok.Value;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.rest.BaseDetails;
import pl.sknikod.kodemy.user.User;

@Value
public class SingleGradeResponse {
    Long id;
    Double grade;
    UserDeatails createdBy;

    @EqualsAndHashCode(callSuper = true)
    @Value
    public static class UserDeatails extends BaseDetails {
        public UserDeatails(Long id, String name) {
            super(id,name);
        }
    }
}
