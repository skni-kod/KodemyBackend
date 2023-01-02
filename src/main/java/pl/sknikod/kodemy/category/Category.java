package pl.sknikod.kodemy.category;

import pl.sknikod.kodemy.material.Material;
import pl.sknikod.kodemy.role.Role;
import pl.sknikod.kodemy.role.RoleName;
import pl.sknikod.kodemy.section.Section;


import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

@Entity
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    private String name;
    @ManyToOne
    @JoinColumn(name="category_id")
    private Section section;

    @OneToMany(mappedBy = "category")
    private Set<Material> materials = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Material> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<Material> materials) {
        this.materials = materials;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Category{" + "id=" + id + ", name='" + name + '\'' + ", materials=" + materials + '}';
    }

    public boolean addMaterial(Material material) {
        return materials.add(material);
    }

    public boolean removeMaterial(Material material) {
        if (materials == null)
            return false;
        return materials.remove(material);
    }
}

