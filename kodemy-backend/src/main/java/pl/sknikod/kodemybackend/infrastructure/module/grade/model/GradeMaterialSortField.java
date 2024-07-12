package pl.sknikod.kodemybackend.infrastructure.module.grade.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GradeMaterialSortField {
    VALUE("value"), AUTHOR("author");

    private final String field;
}