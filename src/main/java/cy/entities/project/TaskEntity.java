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
@HistoryLogTitle(title = "task")
@EntityListeners(ProjectListener.class)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Data
@Table(name = "tbl_tasks")
public class TaskEntity extends ProjectBaseEntity{
    @HistoryLogTitle(title = "mức độ ưu tiên")
    private String priority;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="feature_id")
    private FeatureEntity feature;

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='TASK'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "danh sách người phụ trách", isListType = true)
    @Transient
    private List<UserEntity> devTeam;

    @HistoryLogTitle(title = "danh sách người theo dõi", isListType = true)
    @Transient
    private List<UserEntity> followerTeam;

    @HistoryLogTitle(title = "danh sách người có thể xem", isListType = true)
    @Transient
    private List<UserEntity> viewerTeam;

    @HistoryLogTitle(title = "danh sách người review", isListType = true)
    @Transient
    private List<UserEntity> reViewerTeam;
}
