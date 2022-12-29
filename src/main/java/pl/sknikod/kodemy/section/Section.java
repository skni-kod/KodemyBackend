package pl.sknikod.kodemy.section;

import javax.persistence.*;
import java.util.Objects;


@Entity
public class Section {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;
    @Enumerated(EnumType.STRING)
    private SectionName name;

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
                '}';
    }
}
