package pl.sknikod.kodemysearch.infrastructure.search.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemysearch.infrastructure.search.QueueConsumer;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialSingleResponse {
    private Long id;
    private String title;
    private String description;
    private String link;
    private QueueConsumer.MaterialEvent.MaterialStatus status;
    private boolean isActive;
    private double avgGrade;
    private String author;
    private Date createdDate;
    private Long sectionId;
    private Long categoryId;
    private List<QueueConsumer.MaterialEvent.Technology> technologies;
}