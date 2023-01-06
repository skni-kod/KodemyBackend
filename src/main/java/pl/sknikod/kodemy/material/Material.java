package pl.sknikod.kodemy.material;

import pl.sknikod.kodemy.category.Category;
import pl.sknikod.kodemy.grade.Grade;
import pl.sknikod.kodemy.technology.Technology;
import pl.sknikod.kodemy.type.Type;
import pl.sknikod.kodemy.user.User;

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
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;
    @ManyToMany(mappedBy = "materials")
    private Set<Technology> technologies = new HashSet<>();
    @OneToMany(mappedBy = "material")
    private Set<Grade> grades  = new HashSet<>();
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Set<Technology> getTechnologies() {
        return technologies;
    }

    public void setTechnologies(Set<Technology> technologies) {
        this.technologies = technologies;
    }

    public Set<Grade> getGrades() {
        return grades;
    }

    public void setGrades(Set<Grade> grades) {
        this.grades = grades;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return isActive == material.isActive && Objects.equals(id, material.id) && Objects.equals(title, material.title) && Objects.equals(description, material.description) && status == material.status && Objects.equals(createdDate, material.createdDate);
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
                ", status=" + status +
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

    public boolean addGrade(Grade grade){
        return grades.add(grade);
    }

    public boolean removeGrade(Grade grade){
        if (grades == null)
            return false;
        return grades.remove(grades);
    }
}