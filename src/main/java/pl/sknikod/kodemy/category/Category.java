package pl.sknikod.kodemy.category;
import pl.sknikod.kodemy.section.Section;


import javax.persistence.*;
import java.util.Objects;
import java.util.Set;
import java.util.HashSet;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    private String name;
    @OneToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.MERGE,
            CascadeType.PERSIST,
    })
    @JoinTable(
            name = "category_section",
            joinColumns = @JoinColumn(name = "section_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<Section> sections = new HashSet<>();

    public long getId(){
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName(){
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setSections(Set<Section> sections) {
        this.sections = sections;
    }
    public Set<Section> getSection(){
        return sections;
    }
    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
    @Override
    public String toString(){
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", sections='" + sections + '\'' +
                '}';
    }
    public boolean addSection(Section section){
        return sections.add(section);
    }
}

