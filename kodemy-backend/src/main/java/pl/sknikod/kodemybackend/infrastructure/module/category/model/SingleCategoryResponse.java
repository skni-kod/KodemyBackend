package pl.sknikod.kodemybackend.infrastructure.module.category.model;

import pl.sknikod.kodemybackend.infrastructure.module.section.model.SingleSectionInfoResponse;

public record SingleCategoryResponse(
        Long id,
        String name,
        SingleSectionInfoResponse section
) {
}
