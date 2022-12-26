package pl.sknikod.kodemy.grade;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;

@Entity
@Table(name = "grade")
public class Grade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private long id;

    //   @ManyToOne
//   @JoinColumn(name = user_id)
//   private Set<User> users = new HashSet<>();
//    @ManyToOne
//    @JoinColumn(name = material_id)
//    private Set<Material> materials = new HashSet<>();
    public Grade() {
    }

    public Grade(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    //    public void setUsers(Set<User> users) {
//        this.users = users;
//    }
//    public Set<User> getUsers(){
//        return users;
//    }
//
//    public void setMaterials(Set<Material> materials) {
//        this.materials = materials;
//    }
//    public Set<Material> getMaterials(){
//        return materials;
//    }
//    public boolean addUser(User user) {
//        return users.add(user);
//    }
//
//    public boolean addMaterial(Material material) {
//        return materials.add(material);
//    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Grade{" +
                "id=" + id +
                //  ", users='" + users + '\'' +
                // ", materials='" + materials + '\'' +
                '}';
    }


}
