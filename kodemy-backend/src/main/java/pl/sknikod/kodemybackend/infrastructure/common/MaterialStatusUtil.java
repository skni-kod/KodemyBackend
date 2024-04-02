package pl.sknikod.kodemybackend.infrastructure.common;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

import java.util.List;

@Component
public class MaterialStatusUtil {
    private final List<Material.MaterialStatus> approvedNextStatuses = List.of(
            Material.MaterialStatus.DRAFT,
            Material.MaterialStatus.DELETED,
            Material.MaterialStatus.DEPRECATION_REQUEST,
            Material.MaterialStatus.PENDING
    );
    private final List<Material.MaterialStatus> draftNextStatuses = List.of(
            Material.MaterialStatus.PENDING,
            Material.MaterialStatus.DELETED
    );
    private final List<Material.MaterialStatus> pendingNextStatuses = List.of(
            Material.MaterialStatus.APPROVED,
            Material.MaterialStatus.REJECTED,
            Material.MaterialStatus.BAN_REQUESTED,
            Material.MaterialStatus.BANNED
    );
    private final List<Material.MaterialStatus> deprecationRequestedNextStatuses = List.of(
            Material.MaterialStatus.APPROVED,
            Material.MaterialStatus.DEPRECATED
    );
    private final List<Material.MaterialStatus> deprecatedNextStatuses = List.of(
            Material.MaterialStatus.DELETED
    );
    private final List<Material.MaterialStatus> deletedNextStatuses = List.of();
    private final List<Material.MaterialStatus> rejectedNextStatuses = List.of(
            Material.MaterialStatus.DRAFT,
            Material.MaterialStatus.PENDING
    );
    private final List<Material.MaterialStatus> banRequestedNextStatuses = List.of(
            Material.MaterialStatus.BANNED,
            Material.MaterialStatus.REJECTED
    );
    private final List<Material.MaterialStatus> bannedNextStatuses = List.of(
            Material.MaterialStatus.REJECTED
    );

    public List<Material.MaterialStatus> getPossibleStatuses(Material.MaterialStatus status) {
        switch (status) {
            case APPROVED -> {
                return approvedNextStatuses;
            }
            case DRAFT -> {
                return draftNextStatuses;
            }
            case PENDING -> {
                return pendingNextStatuses;
            }
            case DEPRECATION_REQUEST -> {
                return deprecationRequestedNextStatuses;
            }
            case DEPRECATED -> {
                return deprecatedNextStatuses;
            }
            case DELETED -> {
                return deletedNextStatuses;
            }
            case REJECTED -> {
                return rejectedNextStatuses;
            }
            case BAN_REQUESTED -> {
                return banRequestedNextStatuses;
            }
            case BANNED -> {
                return bannedNextStatuses;
            }
        }
        return List.of(status);
    }

    public SimpleGrantedAuthority getAuthorityForStatusChange(
            Material.MaterialStatus current,
            Material.MaterialStatus next
    ) {
        switch (current) {
            case DEPRECATION_REQUEST -> {
                return new SimpleGrantedAuthority("CAN_DEPRECATE_MATERIAL");
            }
            case PENDING -> {
                return new SimpleGrantedAuthority("CAN_APPROVED_MATERIAL");
            }
            case BAN_REQUESTED -> {
                return new SimpleGrantedAuthority("CAN_BAN_MATERIAL");
            }
            case BANNED -> {
                return new SimpleGrantedAuthority("CAN_UNBAN_MATERIAL");
            }
        }
        return null;
    }
}
