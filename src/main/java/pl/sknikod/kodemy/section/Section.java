package pl.sknikod.kodemy.section;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemy.category.Category;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "sections")
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Enumerated(EnumType.STRING)
    private SectionName name;
    @OneToMany(mappedBy = "section")
    private Set<Category> categories = new HashSet<>();

    public Section(SectionName name) {
        this.name = name;
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
                ", name=" + name +
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