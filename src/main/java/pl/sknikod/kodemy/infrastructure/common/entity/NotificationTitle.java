package pl.sknikod.kodemy.infrastructure.common.entity;

import lombok.Getter;

@Getter
public enum NotificationTitle {
    MATERIAL_APPROVED("Twój materiał został zatwierdzony"),
    MATERIAL_REJECTED("Twój materiał został odrzucony"),
    MATERIAL_BANNED("Twój materiał został zbanowany"),
    MATERIAL_APPROVAL_REQUEST("Nowy/editowy materiał wymaga zatwierdzenia");

    private final String desc;

    NotificationTitle(String desc) {
        this.desc = desc;
    }
}
