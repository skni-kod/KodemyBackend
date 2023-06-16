package pl.sknikod.kodemy.infrastructure.rest.model;

import lombok.Value;

import java.util.List;

@Value
public class SingleSectionResponse {
    Long id;
    String name;

    List<CategoryDetails> categories;

    @Value
    public static class CategoryDetails {
        Long id;
        String name;
    }
}
