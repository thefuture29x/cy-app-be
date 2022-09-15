package cy.entities.project;

import cy.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

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

    @Column(name = "files")
    private String attachFiles;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="id_user_assign")
    private UserEntity assignTo;


}
