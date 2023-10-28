package pl.sknikod.kodemysearch.infrastructure.search.rest;

import lombok.*;
import pl.sknikod.kodemysearch.infrastructure.material.MaterialStatus;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaterialResponse {
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
    private List<Long> technologyIds;
}
