package pl.sknikod.kodemysearch.infrastructure.module.material.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public record MaterialPageable(
        Long id,
        String title,
        String description,
        MaterialStatus status,
        boolean isActive,
        double avgGrade,
        AuthorDetails author,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        Date createdDate,
        Long sectionId,
        Long categoryId,
        List<TagDetails> tags
) {
    public enum MaterialStatus {
        APPROVED,
        PENDING,
        REJECTED,
        EDITED,
        BANNED,
        DRAFT,
        BAN_REQUESTED,
        DEPRECATION_REQUEST,
        DEPRECATED,
        DELETED
    }

    public record TagDetails(Long id, String name) {
    }

    public record AuthorDetails(Long id, String username) {
    }
}
