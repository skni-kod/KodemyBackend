package pl.sknikod.kodemybackend.infrastructure.database;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemycommon.data.BaseEntity;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "grades")
public class Grade extends BaseEntity {
    @Column(name = "grade", precision = 3)
    private Double value;
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Grade grade)) return false;
        if (!super.equals(object)) return false;
        return Objects.equals(value, grade.value) && Objects.equals(userId, grade.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), value, userId);
    }
}