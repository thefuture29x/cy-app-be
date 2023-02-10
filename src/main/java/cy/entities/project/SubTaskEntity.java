package cy.entities.project;

import cy.entities.UserEntity;
import cy.entities.project.Listener.ProjectListener;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@HistoryLogTitle(title = "sub task")
@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_sub_tasks")
public class SubTaskEntity extends ProjectBaseEntity {
    @HistoryLogTitle(title = "mức độ ưu tiên")
    private String priority;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private TaskEntity task;

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='SUBTASK'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "assign to" )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user_assign")
    private UserEntity assignTo;

    @HistoryLogTitle(title = "assign tester")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tester_assign")
    private UserEntity assignToTester;

    @HistoryLogTitle(title = "danh sách người phụ trách", isListType = true)
    @Transient
    private List<UserEntity> devTeam;

    @HistoryLogTitle(title = "danh sách người theo dõi", isListType = true)
    @Transient
    private List<UserEntity> followerTeam;
}
