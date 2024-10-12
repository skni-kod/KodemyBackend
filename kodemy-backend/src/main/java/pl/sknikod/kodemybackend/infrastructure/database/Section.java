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
@Table(name = "sections")
public class Section extends BaseEntity {
    @Column(unique = true, nullable = false)
    private String name;
    @Column(unique = true, nullable = false)
    private String prefix;
    @OneToMany(mappedBy = "section")
    private Set<Category> categories = new HashSet<>();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Section section)) return false;
        if (!super.equals(object)) return false;
        return Objects.equals(name, section.name) && Objects.equals(prefix, section.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, prefix);
    }
}