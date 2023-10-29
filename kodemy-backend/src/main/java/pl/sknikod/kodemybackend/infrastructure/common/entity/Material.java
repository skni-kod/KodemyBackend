package pl.sknikod.kodemybackend.infrastructure.common.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;
import pl.sknikod.kodemybackend.util.Auditable;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "materials")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Material extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @NotEmpty(message = "Title may not be empty")
    @Size(min = 2, max = 32, message = "Title must be between 2 and 32 characters long")
    private String title;
    @NotEmpty(message = "Description may not be empty")
    private String description;
    private String link;
    @Enumerated(EnumType.STRING)
    private MaterialStatus status;
    @Column(nullable = false)
    private boolean isActive;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "technologies_materials",
            joinColumns = @JoinColumn(name = "technology_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id")
    )
    private Set<Technology> technologies = new HashSet<>();
    @OneToMany(mappedBy = "material")
    private Set<Grade> grades = new HashSet<>();
    @org.hibernate.annotations.Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private UserJsonB author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return isActive == material.isActive && Objects.equals(id, material.id) && Objects.equals(title, material.title) && Objects.equals(description, material.description) && status == material.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, isActive);
    }

    public enum MaterialStatus {
        APPROVED, //CONFIRMED
        PENDING, //UNCONFIRMED, AWAITING_APPROVAL, PENDING
        REJECTED,
        EDITED, //CORRECTED
        BANNED
    }
}