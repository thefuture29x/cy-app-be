package cy.entities.project;

import cy.dtos.project.FeatureDto;
import cy.entities.UserEntity;
import cy.entities.project.Listener.ProjectListener;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
@HistoryLogTitle(title = "feature")
@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_features")
public class FeatureEntity extends ProjectBaseEntity{
    @HistoryLogTitle(title = "mức độ ưu tiên")
    private String priority;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="project_id")
    private ProjectEntity project;

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='FEATURE'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "danh sách người phụ trách", isListType = true)
    @Transient
    private List<UserEntity> devTeam = new ArrayList<>();

    @HistoryLogTitle(title = "danh sách người theo dõi", isListType = true)
    @Transient
    private List<UserEntity> followTeam;

    @HistoryLogTitle(title = "danh sách người có thể xem", isListType = true)
    @Transient
    private List<UserEntity> viewTeam;

}
