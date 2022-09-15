package cy.entities.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_projects")
public class ProjectEntity extends ProjectBaseEntity{

//    @OneToMany
////    @JoinColumn(name = "object_id", insertable = false, updatable = false)
////    @Where(clause = "category='PROJECT'")
//    private List<FileEntity> attachFiles;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
//    @Where(clause = "category='PROJECT'")
    private FileEntity avatar;

}
