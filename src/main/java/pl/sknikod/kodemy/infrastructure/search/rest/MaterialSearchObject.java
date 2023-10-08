package pl.sknikod.kodemy.infrastructure.search.rest;

import lombok.*;
import org.opensearch.index.mapper.KeywordFieldMapper;
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
    private String user;
    private Date createdDate;
    private Long sectionId;
    private Long categoryId;
    private List<Long> technologyIds;
}
