package pl.sknikod.kodemy.infrastructure.category.rest;

import lombok.Data;
import pl.sknikod.kodemy.infrastructure.section.rest.SingleSectionInfoResponse;

@Data
public class SingleCategoryResponse {
    private Long id;
    private String name;
    private SingleSectionInfoResponse section;
}
