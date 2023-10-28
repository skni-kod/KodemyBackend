package pl.sknikod.kodemybackend.infrastructure.material.rest;

import lombok.Value;

import java.util.Set;

@Value
public class MaterialUpdateResponse {
    Long id;
    String title;
    String description;
    String link;
    BaseDetails category;
    BaseDetails type;
    Set<BaseDetails> technologies;

    @Value
    public static class BaseDetails {
        Long id;
        String name;
    }
}

