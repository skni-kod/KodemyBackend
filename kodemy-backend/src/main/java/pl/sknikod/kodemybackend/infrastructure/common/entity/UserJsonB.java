package pl.sknikod.kodemybackend.infrastructure.common.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
public class UserJsonB {
    private Long id;
    private String name;
}
