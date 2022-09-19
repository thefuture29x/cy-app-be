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

@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_projects")
@HistoryLogTitle(title = "project")
public class ProjectEntity extends ProjectBaseEntity{

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='PROJECT'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "avatar")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "avatar_id")
    private FileEntity avatar;


    @HistoryLogTitle(title = "", ignore = true)
    @Transient
    private List<UserEntity> devTeam;

    @HistoryLogTitle(title = "", ignore = true)
    @Transient
    private List<UserEntity> followTeam;

    @HistoryLogTitle(title = "", ignore = true)
    @Transient
    private List<UserEntity> viewTeam;


}
