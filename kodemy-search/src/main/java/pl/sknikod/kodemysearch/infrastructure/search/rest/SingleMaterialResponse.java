package pl.sknikod.kodemysearch.infrastructure.search.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
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
public class SingleMaterialResponse {
    private Long id;
    private String title;
    private String description;
    private QueueConsumer.MaterialEvent.MaterialStatus status;
    private boolean isActive;
    private double avgGrade;
    private QueueConsumer.MaterialEvent.AuthorDetails author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private Date createdDate;
    private Long sectionId;
    private Long categoryId;
    private List<QueueConsumer.MaterialEvent.TechnologyDetails> technologies;
}