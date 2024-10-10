package pl.sknikod.kodemycommons.data;

import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class Auditable {
    protected Long createdBy;
    protected LocalDateTime createdDate;
    protected Long modifiedBy;
    protected LocalDateTime modifiedDate;

    @PrePersist
    protected void onPrePersist() {
        this.createdDate = LocalDateTime.now();
        this.createdBy = AuditorAware.getCurrentAuditor().orElse(null);
    }

    @PreUpdate
    protected void onPreUpdate() {
        this.modifiedDate = LocalDateTime.now();
        this.modifiedBy = AuditorAware.getCurrentAuditor().orElse(null);
    }
}
