package pl.sknikod.kodemy.rest.response;

import lombok.Value;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.user.User;

@Value
public class SingleGradeResponse {
    Long id;
    Double grade;
    UserResponse createdBy;

    public SingleGradeResponse(Grade grade) {
        this.id = grade.getId();
        this.grade = grade.getGrade();
        this.createdBy = new UserResponse(grade.getId(), grade.getUser().getUsername());
    }

    @Value
    public static class UserResponse {
        Long id;
        String name;

        public UserResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }
}
