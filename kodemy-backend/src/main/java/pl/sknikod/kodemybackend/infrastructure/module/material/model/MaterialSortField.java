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
    AUTHOR("author"),
    CREATED_DATE("createdDate"),
    SECTION_ID("sectionId"),
    CATEGORY_ID("categoryId");

    private final String field;
}
