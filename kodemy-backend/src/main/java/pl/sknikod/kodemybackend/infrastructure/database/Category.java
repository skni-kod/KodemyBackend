package pl.sknikod.kodemybackend.infrastructure.database;

import jakarta.persistence.*;
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
@Table(name = "categories")
public class Category extends BaseEntity {
    @Column(nullable = false)
    private String signature;
    @Column(nullable = false)
    private String name;
    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;
    @OneToMany(mappedBy = "category")
    private Set<Material> materials = new HashSet<>();

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Category category)) return false;
        if (!super.equals(object)) return false;
        return Objects.equals(signature, category.signature) && Objects.equals(name, category.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), signature, name);
    }
}