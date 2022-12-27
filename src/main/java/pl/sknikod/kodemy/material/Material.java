package pl.sknikod.kodemy.material;

import pl.sknikod.kodemy.technology.Technology;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Material {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String title;
    private String description;
    @Enumerated(EnumType.STRING)
    private MaterialStatus status;
    private LocalDateTime createdDate;
    private boolean isActive;

    @ManyToMany(fetch = FetchType.EAGER,
               cascade = {
                        CascadeType.PERSIST,
                        CascadeType.MERGE
    })
    @JoinTable(
            name = "material_technology",
            joinColumns =  @JoinColumn(name = "technology_id"),
            inverseJoinColumns = @JoinColumn(name = "material_id")
    )

    private Set<Technology> technologies = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MaterialStatus getStatus() {
        return status;
    }

    public void setStatus(MaterialStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Material material)) return false;
        return isActive == material.isActive && Objects.equals(id, material.id) && Objects.equals(title, material.title) && Objects.equals(description, material.description) && Objects.equals(status, material.status) && Objects.equals(createdDate, material.createdDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, createdDate, isActive);
    }

    @Override
    public String toString() {
        return "Material{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", createdDate=" + createdDate +
                ", isActive=" + isActive +
                '}';
    }

    public boolean addTechnology(Technology technology){
        return technologies.add(technology);
    }

    public boolean removeTechnology(Technology technology){
        if (technologies == null)
            return false;
        return technologies.remove(technology);

    }
}
