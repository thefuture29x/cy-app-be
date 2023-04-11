package cy.entities.mission;

import cy.entities.common.FileEntity;
import cy.entities.common.HistoryLogTitle;
import cy.entities.common.UserEntity;
import cy.entities.project.Listener.ProjectListener;
import cy.entities.project.ProjectBaseEntity;
import cy.entities.project.ProjectEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@HistoryLogTitle(title = "assign")
@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_assign")
public class AssignEntity  extends ProjectBaseEntity {

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="mission_id")
    private MissionEntity mission;

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='ASSIGN'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "danh sách người phụ trách", isListType = true)
    @Transient
    private List<UserEntity> devTeam = new ArrayList<>();

    @HistoryLogTitle(title = "danh sách người theo dõi", isListType = true)
    @Transient
    private List<UserEntity> followTeam;

    @HistoryLogTitle(title = "tính chất")
    private String nature;

    @HistoryLogTitle(title = "phân loại")
    private String type;

}