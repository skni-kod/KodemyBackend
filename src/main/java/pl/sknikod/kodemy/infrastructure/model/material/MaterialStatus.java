package pl.sknikod.kodemy.infrastructure.model.material;

public enum MaterialStatus {
    APPROVED, //CONFIRMED
    PENDING, //UNCONFIRMED, AWAITING_APPROVAL, PENDING
    REJECTED,
    EDITED, //CORRECTED
    BANNED
}