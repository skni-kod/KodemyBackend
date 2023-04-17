package pl.sknikod.kodemy.rest.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.user.User;

import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class MaterialShowGradesResponse {
    private Set<GradeResponse> grades;

    @Getter
    @Setter
    @NoArgsConstructor
    public static class GradeResponse {
        private Long id;
        private Double grade;
        private UserResponse createdBy;

        public GradeResponse(Long id, Double grade, User createdBy) {
            this.id = id;
            this.grade = grade;
            this.createdBy = new UserResponse(createdBy.getId(), createdBy.getUsername());
        }
    }

    @Getter
    @Setter
    @NoArgsConstructor
    public static class UserResponse {
        private Long id;
        private String name;

        public UserResponse(Long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public MaterialShowGradesResponse(Set<Grade> grades) {
        this.grades = grades.stream().map(grade -> {
            GradeResponse gradeResponse = new GradeResponse(grade.getId(), grade.getGrade(), grade.getUser());
            return gradeResponse;
        }).collect(Collectors.toSet());
    }
}
