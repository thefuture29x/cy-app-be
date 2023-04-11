package cy.entities.mission;

import cy.entities.common.UserEntity;
import cy.entities.common.FileEntity;
import cy.entities.common.HistoryLogTitle;
import cy.entities.project.Listener.ProjectListener;
import cy.entities.project.ProjectBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_mission")
@HistoryLogTitle(title = "mission")
public class MissionEntity extends ProjectBaseEntity {

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='MISSION'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "danh sách người phụ trách", isListType = true)
    @Transient
    private List<UserEntity> devTeam;

    @HistoryLogTitle(title = "danh sách người theo dõi", isListType = true)
    @Transient
    private List<UserEntity> followTeam;

    @HistoryLogTitle(title = "danh sách người có thể xem", isListType = true)
    @Transient
    private List<UserEntity> viewTeam;

    @HistoryLogTitle(title = "tính chất")
    private String nature;

    @HistoryLogTitle(title = "phân loại")
    private String type;

    @HistoryLogTitle(title = "assignment")
    private Boolean isAssign = false;

}
