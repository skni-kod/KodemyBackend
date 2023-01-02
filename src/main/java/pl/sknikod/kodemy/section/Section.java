package pl.sknikod.kodemy.section;

import pl.sknikod.kodemy.category.Category;
import pl.sknikod.kodemy.technology.Technology;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Enumerated(EnumType.STRING)
    private SectionName name;

    @OneToMany(mappedBy = "section")
    private Set<Category> categories = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public SectionName getName() {
        return name;
    }

    public Section() {
    }

    public Section(SectionName name) {
        this.name = name;
    }

    public void setName(SectionName name) {
        this.name = name;
    }

    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Section section = (Section) o;
        return Objects.equals(id, section.id) && name == section.name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categories='" + categories + '\'' +
                '}';
    }

    public boolean addCategory(Category category){
        return categories.add(category);
    }

    public boolean removeCategory(Category category){
        if (categories == null)
            return false;
        return categories.remove(category);
    }
}
