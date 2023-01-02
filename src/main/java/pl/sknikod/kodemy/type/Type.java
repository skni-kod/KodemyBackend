package pl.sknikod.kodemy.type;

import pl.sknikod.kodemy.category.Category;
import pl.sknikod.kodemy.material.Material;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Type {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;
    private String name;

    @OneToMany(mappedBy = "type")
    private Set<Material> materials = new HashSet<>();

    public Type() {
    }

    public Type(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return Objects.equals(id, type.id) && name == type.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Type{" +
                "id=" + id +
                ", name=" + name +
                ", materials=" + materials +
                '}';
    }
    public boolean addMaterial(Material material){
        return materials.add(material);
    }

    public boolean removeMaterial(Material material){
        if (materials == null)
            return false;
        return materials.remove(material);
    }
}
