package pl.sknikod.kodemyauth.infrastructure.module.oauth2.engine;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Registration {
    github("github");

    private final String id;
}
