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
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Grade{" + "id=" + id + '}';
    }
}
