package pl.sknikod.kodemybackend.infrastructure.common.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pl.sknikod.kodemybackend.util.Auditable;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "materials")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Material extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
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
            name = "technologies_materials",
            joinColumns = @JoinColumn(name = "material_id"),
            inverseJoinColumns = @JoinColumn(name = "technology_id")
    )
    private Set<Technology> technologies = new HashSet<>();
    @OneToMany(mappedBy = "material")
    private Set<Grade> grades = new HashSet<>();
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "author_id")
    private Author author;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Material material = (Material) o;
        return isActive == material.isActive && Objects.equals(id, material.id) && Objects.equals(title, material.title) && Objects.equals(description, material.description) && status == material.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, status, isActive);
    }

    public enum MaterialStatus {
        APPROVED,
        DRAFT,
        PENDING,
        REJECTED,
        EDITED,
        BAN_REQUESTED,
        BANNED,
        DEPRECATION_REQUESTED,
        DEPRECATED,
        DELETED
    }

    public static Map<MaterialStatus, GrantedAuthority> getPossibleStatuses(MaterialStatus current) {
        var possible = new HashMap<MaterialStatus, GrantedAuthority>();
        switch (current) {
            case APPROVED -> {
                var authority = new SimpleGrantedAuthority("CAN_EDIT_MATERIAL");
                possible.put(MaterialStatus.DRAFT, authority);
                possible.put(MaterialStatus.DEPRECATION_REQUESTED, authority);
                possible.put(MaterialStatus.DELETED, authority);
                possible.put(MaterialStatus.PENDING, authority);
            }
            case DRAFT -> {
                var authority = new SimpleGrantedAuthority("CAN_EDIT_MATERIAL");
                possible.put(MaterialStatus.DELETED, authority);
                possible.put(MaterialStatus.PENDING, authority);
            }
            case PENDING -> {
                var authority = new SimpleGrantedAuthority("CAN_APPROVED_MATERIAL");
                possible.put(MaterialStatus.APPROVED, authority);
                possible.put(MaterialStatus.REJECTED, authority);
                possible.put(MaterialStatus.BAN_REQUESTED, authority);
            }
            case REJECTED -> {
                var authority = new SimpleGrantedAuthority("CAN_EDIT_MATERIAL");
                possible.put(MaterialStatus.DRAFT, authority);
                possible.put(MaterialStatus.PENDING, authority);
            }
            case BAN_REQUESTED -> {
                var authority = new SimpleGrantedAuthority("CAN_BAN_DEPRECATE_MATERIAL");
                possible.put(MaterialStatus.BANNED, authority);
            }
            case BANNED -> {
                var authority = new SimpleGrantedAuthority("CAN_UNBAN_MATERIAL");
                possible.put(MaterialStatus.REJECTED, authority);
            }
            case DEPRECATION_REQUESTED -> {
                var authority = new SimpleGrantedAuthority("CAN_BAN_DEPRECATE_MATERIAL");
                possible.put(MaterialStatus.APPROVED, authority);
                possible.put(MaterialStatus.DEPRECATED, authority);
            }
            case DEPRECATED -> {
                var authority = new SimpleGrantedAuthority("CAN_EDIT_MATERIAL");
                possible.put(MaterialStatus.DELETED, authority);
            }
        }
        return possible;
    }
}