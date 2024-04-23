package pl.sknikod.kodemyauth.infrastructure.user.rest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserSortField {
    ID("id"), USERNAME("username"), EMAIL("email"), ROLE("role"), IS_EXPIRED("isExpired"), IS_ENABLED("isEnabled");

    private final String field;
}
