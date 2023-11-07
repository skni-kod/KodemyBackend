package pl.sknikod.kodemysearch.infrastructure.search.rest;

import lombok.*;
import pl.sknikod.kodemysearch.infrastructure.search.QueueConsumer;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SingleMaterialResponse {
    private Long id;
    private String title;
    private String description;
    private String link;
    private QueueConsumer.MaterialEvent.MaterialStatus status;
    private boolean isActive;
    private double avgGrade;
    private UserResponse creator;
    private Date createdDate;
    private Long sectionId;
    private Long categoryId;
    private List<QueueConsumer.MaterialEvent.Technology> technologies;

    @Data
    public static class UserResponse{
        private Long id;
        private String username;
    }
}