package pl.sknikod.kodemy.infrastructure.model.entity;

public enum MaterialStatus {
    APPROVED, //CONFIRMED
    PENDING, //UNCONFIRMED, AWAITING_APPROVAL, PENDING
    REJECTED,
    EDITED, //CORRECTED
    BANNED
}