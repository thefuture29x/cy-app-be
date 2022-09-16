package cy.entities.project;

import cy.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_sub_tasks")
public class SubTaskEntity extends ProjectBaseEntity{

    private String priority;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name ="task_id")
    private TaskEntity task;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='SUBTASK'")
    private List<FileEntity> attachFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="id_user_assign")
    private UserEntity assignTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="id_tester_assign")
    private UserEntity assignToTester;

    @Transient
    private List<UserEntity> devTeam;
}
