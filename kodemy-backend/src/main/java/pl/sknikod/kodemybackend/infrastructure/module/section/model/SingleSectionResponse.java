package pl.sknikod.kodemybackend.infrastructure.module.section.model;

import java.util.List;

public record SingleSectionResponse(
        Long id,
        String name,
        List<CategoryDetails> categories
) {
    public record CategoryDetails(
            Long id,
            String name
    ) {
    }
}
