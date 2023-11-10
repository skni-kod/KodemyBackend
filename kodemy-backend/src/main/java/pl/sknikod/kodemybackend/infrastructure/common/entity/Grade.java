package pl.sknikod.kodemybackend.infrastructure.common.entity;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.TypeDef;
import pl.sknikod.kodemybackend.util.Auditable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "grades")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Grade extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    @Column(name = "grade", precision = 3, scale = 2)
    private Double value;
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(name = "author_id")
    private Author author;
    @ManyToOne
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Grade grade1)) return false;
        return Objects.equals(id, grade1.id) && Objects.equals(value, grade1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}