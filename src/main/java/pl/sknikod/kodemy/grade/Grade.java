package pl.sknikod.kodemy.grade;

import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.user.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Grade {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Grade grade = (Grade) o;
        return Objects.equals(id, grade.id) && Objects.equals(grade, grade.grade) && Objects.equals(user, grade.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, grade, user);
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                ", value=" + grade +
                '}';
    }
}