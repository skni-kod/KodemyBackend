package pl.sknikod.kodemybackend.infrastructure.database.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.sknikod.kodemybackend.util.data.BaseEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "materials")
public class Material extends BaseEntity {
    @NotEmpty(message = "Title may not be empty")
    @Size(min = 2, max = 32, message = "Title must be between 2 and 32 characters long")
    private String title;
    @NotEmpty(message = "Description may not be empty")
    private String description;
    private String link;
    @Enumerated(EnumType.STRING)
    private MaterialStatus status;
    @Column(nullable = false)
    private boolean isActive;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private Type type;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "tags_materials",
            joinColumns = @JoinColumn(name = "material_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();
    @OneToMany(mappedBy = "material")
    private Set<Grade> grades = new HashSet<>();
    @Column(name = "user_id", nullable = false, updatable = false)
    private Long userId;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Material material)) return false;
        if (!super.equals(object)) return false;
        return isActive == material.isActive && Objects.equals(title, material.title) && Objects.equals(description, material.description) && Objects.equals(link, material.link) && status == material.status && Objects.equals(userId, material.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), title, description, link, status, isActive, userId);
    }

    public enum MaterialStatus {
        APPROVED,
        PENDING,
        DRAFT,
        REJECTED,
        BAN_REQUESTED,
        BANNED,
        DEPRECATION_REQUEST,
        DEPRECATED,
        DELETED
    }
}