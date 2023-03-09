package pl.sknikod.kodemy.material;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.category.Category;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.technology.Technology;
import pl.sknikod.kodemy.type.Type;
import pl.sknikod.kodemy.user.User;
import pl.sknikod.kodemy.util.Auditable;


import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "materials")
public class Material extends Auditable<String>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private MaterialStatus status;
    private LocalDateTime createdDate;
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
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return isActive == material.isActive && Objects.equals(id, material.id) && Objects.equals(title, material.title) && Objects.equals(description, material.description) && status == material.status && Objects.equals(createdDate, material.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, createdDate, isActive);
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", isActive=" + isActive +
                '}';
    }

    public boolean addTechnology(Technology technology) {
        return technologies.add(technology);
    }

    public boolean removeTechnology(Technology technology) {
        if (technologies == null)
            return false;
        return technologies.remove(technology);
    }

    public boolean addGrade(Grade grade) {
        return grades.add(grade);
    }

    public boolean removeGrade(Grade grade) {
        if (grades == null)
            return false;
        return grades.remove(grade);
    }
}