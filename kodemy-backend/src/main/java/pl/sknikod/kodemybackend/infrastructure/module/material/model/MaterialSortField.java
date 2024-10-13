package pl.sknikod.kodemybackend.infrastructure.module.material.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MaterialSortField {
    ID("id"),
    TITLE("title"),
    DESCRIPTION("description"),
    STATUS("status"),
    IS_ACTIVE("isActive"),
    AVG_GRADE("avgGrade"),
    CREATED_DATE("createdDate");

    private final String field;
}
