package pl.sknikod.kodemybackend.infrastructure.category.rest;

import lombok.Data;
import pl.sknikod.kodemybackend.infrastructure.section.rest.SingleSectionInfoResponse;

@Data
public class SingleCategoryResponse {
    private Long id;
    private String name;
    private SingleSectionInfoResponse section;
}
