package pl.sknikod.kodemy.infrastructure.model.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.util.Auditable;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "materials")
public class Material extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String title;
    private String description;
    private String link;
    @Enumerated(EnumType.STRING)
    private MaterialStatus status;
    private boolean isActive;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;
    @ManyToMany(mappedBy = "materials")
    private Set<Technology> technologies = new HashSet<>();
    @OneToMany(mappedBy = "material")
    private Set<Grade> grades = new HashSet<>();
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", isActive=" + isActive +
                "} " + super.toString();
    }

}