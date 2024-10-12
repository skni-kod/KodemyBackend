package pl.sknikod.kodemybackend.infrastructure.database;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemycommons.data.BaseEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "types")
public class Type extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
    @OneToMany(mappedBy = "type")
    private Set<Material> materials = new HashSet<>();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Type type)) return false;
        if (!super.equals(object)) return false;
        return Objects.equals(name, type.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name);
    }
}