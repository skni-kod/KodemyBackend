package pl.sknikod.kodemyauth.infrastructure.module.auth.model;

public record AuthInfoResponse(
        Long id,
        String username,
        RoleDetails role
) {
    public record RoleDetails(Long id, String name) {
    }
}
