package pl.sknikod.kodemy.infrastructure.search.rest;

import lombok.*;
import pl.sknikod.kodemy.infrastructure.common.entity.MaterialStatus;

import java.util.Date;

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
    private Long categoryId;
}
