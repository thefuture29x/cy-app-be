package cy.entities.project;

import cy.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_bugs")
public class BugEntity extends ProjectBaseEntity{

    private String priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="sub_task_id")
    private SubTaskEntity subTask;

    @OneToMany
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    @Where(clause = "category='tbl_bugs'")
    private List<FileEntity> attachFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="id_user_assign")
    private UserEntity assignTo;

    @ManyToMany
    @WhereJoinTable(clause = "category='tbl_bugs'")
            @JoinTable(name = "tbl_tag_relations",
            joinColumns = @JoinColumn(name = "object_id", insertable = false, updatable = false),
            inverseJoinColumns = {@JoinColumn(name = "tag_id", insertable = false, updatable = false)})
    Set<TagEntity> tagEntities;

}
