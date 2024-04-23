package pl.sknikod.kodemybackend.infrastructure.common;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import pl.sknikod.kodemybackend.infrastructure.common.entity.Material;

import java.util.Collections;
import java.util.List;

public class MaterialStatusUtil {
    private static final List<Material.MaterialStatus> approvedNextStatuses = List.of(
            Material.MaterialStatus.DRAFT,
            Material.MaterialStatus.DELETED,
            Material.MaterialStatus.DEPRECATION_REQUEST,
            Material.MaterialStatus.PENDING
    );
    private static final List<Material.MaterialStatus> draftNextStatuses = List.of(
            Material.MaterialStatus.PENDING,
            Material.MaterialStatus.DELETED
    );
    private static final List<Material.MaterialStatus> pendingNextStatuses = List.of(
            Material.MaterialStatus.APPROVED,
            Material.MaterialStatus.REJECTED,
            Material.MaterialStatus.BAN_REQUESTED,
            Material.MaterialStatus.BANNED
    );
    private static final List<Material.MaterialStatus> deprecationRequestedNextStatuses = List.of(
            Material.MaterialStatus.APPROVED,
            Material.MaterialStatus.DEPRECATED
    );
    private static final List<Material.MaterialStatus> deprecatedNextStatuses = List.of(
            Material.MaterialStatus.DELETED);
    private static final List<Material.MaterialStatus> deletedNextStatuses = Collections.emptyList();
    private static final List<Material.MaterialStatus> rejectedNextStatuses = List.of(
            Material.MaterialStatus.DRAFT,
            Material.MaterialStatus.PENDING
    );
    private static final List<Material.MaterialStatus> banRequestedNextStatuses = List.of(
            Material.MaterialStatus.BANNED,
            Material.MaterialStatus.REJECTED
    );
    private static final List<Material.MaterialStatus> bannedNextStatuses = List.of(
            Material.MaterialStatus.REJECTED
    );

    public static List<Material.MaterialStatus> getPossibleStatuses(Material.MaterialStatus status) {
        return switch (status) {
            case APPROVED -> approvedNextStatuses;
            case DRAFT -> draftNextStatuses;
            case PENDING -> pendingNextStatuses;
            case DEPRECATION_REQUEST -> deprecationRequestedNextStatuses;
            case DEPRECATED -> deprecatedNextStatuses;
            case DELETED -> deletedNextStatuses;
            case REJECTED -> rejectedNextStatuses;
            case BAN_REQUESTED -> banRequestedNextStatuses;
            case BANNED -> bannedNextStatuses;
        };
    }

    public static SimpleGrantedAuthority getAuthorityForStatusChange(
            Material.MaterialStatus current,
            Material.MaterialStatus next
    ) {
        return switch (current) {
            case DEPRECATION_REQUEST -> new SimpleGrantedAuthority("CAN_DEPRECATE_MATERIAL");
            case PENDING -> new SimpleGrantedAuthority("CAN_APPROVED_MATERIAL");
            case BAN_REQUESTED -> new SimpleGrantedAuthority("CAN_BAN_MATERIAL");
            case BANNED -> new SimpleGrantedAuthority("CAN_UNBAN_MATERIAL");
            default -> null;
        };
    }
}
