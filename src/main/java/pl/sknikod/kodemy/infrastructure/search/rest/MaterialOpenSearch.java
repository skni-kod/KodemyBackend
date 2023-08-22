package pl.sknikod.kodemy.infrastructure.search.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.infrastructure.common.entity.MaterialStatus;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MaterialOpenSearch {
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
