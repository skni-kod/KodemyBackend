package pl.sknikod.kodemy.category;

import pl.sknikod.kodemy.material.Material;
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
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name="section_id")
    private Section section;
    @OneToMany(mappedBy = "category")
    private Set<Material> materials = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
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
        Category category = (Category) o;
        return Objects.equals(id, category.id) && Objects.equals(name, category.name) && Objects.equals(section, category.section);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, section);
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