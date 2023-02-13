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
@HistoryLogTitle(title = "bug")
@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_bugs")
public class BugEntity extends ProjectBaseEntity{

    @HistoryLogTitle(title = "mức độ ưu tiên")
    private String priority;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="sub_task_id")
    private SubTaskEntity subTask;

    @HistoryLogTitle(title = "" ,ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="task_id")
    private TaskEntity task;

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='BUG'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="id_user_assign")
    private UserEntity assignTo;//người chịu trách nhiệm chính trong fixbug


    @HistoryLogTitle(title = "từ khóa", isTagFields = true)
    @Transient
    private List<TagEntity> tagList;

    @HistoryLogTitle(title = "danh sách người phụ trách", isListType = true)
    @Transient
    private List<UserEntity> responsibleList;//danh sách người phụ trách

    @HistoryLogTitle(title = "danh sách người theo dõi", isListType = true)
    @Transient
    private List<UserEntity> reviewerList;//danh sách người review

    @HistoryLogTitle(title = "", ignore = true)
    @OneToMany(mappedBy = "bugId")
    private List<BugHistoryEntity> historyBugList;
    @HistoryLogTitle(title = "lý do")
    @Transient
    private String reason;

}
