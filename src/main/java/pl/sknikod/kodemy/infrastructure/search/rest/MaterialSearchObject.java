package pl.sknikod.kodemy.infrastructure.search.rest;

import lombok.*;
import pl.sknikod.kodemy.infrastructure.common.entity.MaterialStatus;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialSearchObject {
    private Long id;
    private String title;
    private String description;
    private String link;
    private MaterialStatus status;
    private boolean isActive;
    private double avgGrade;
    private String user;
    private Date createdDate;
    private Long sectionId;
    private Long categoryId;
    private List<Technology> technologies;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Technology {
        private Long id;
        private String name;
    }
}
