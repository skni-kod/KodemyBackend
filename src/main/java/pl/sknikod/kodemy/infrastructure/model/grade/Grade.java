package pl.sknikod.kodemy.infrastructure.model.grade;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.infrastructure.model.user.User;
import pl.sknikod.kodemy.infrastructure.model.material.Material;
import pl.sknikod.kodemy.util.Auditable;

import javax.persistence.*;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "grades")
public class Grade extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(precision = 3, scale = 2)
    private Double grade;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grade grade1)) return false;
        return Objects.equals(id, grade1.id) && Objects.equals(grade, grade1.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, grade);
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", grade=" + grade +
                "} " + super.toString();
    }
}