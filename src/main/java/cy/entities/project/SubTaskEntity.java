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
@Table(name = "tbl_sub_tasks")
public class SubTaskEntity extends ProjectBaseEntity{

    private String priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="task_id")
    private TaskEntity task;

    @OneToMany
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    @Where(clause = "category='tbl_sub_tasks'")
    private List<FileEntity> attachFiles;

    @ManyToMany
    @WhereJoinTable(clause = "category='tbl_sub_tasks'")
    @JoinTable(name = "tbl_tag_relations",
            joinColumns = @JoinColumn(name = "object_id", insertable = false, updatable = false),
            inverseJoinColumns = {@JoinColumn(name = "tag_id", insertable = false, updatable = false)})
    Set<TagEntity> tagEntities;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="id_user_assign")
    private UserEntity assignTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="id_tester_assign")
    private UserEntity assignToTester;
}
