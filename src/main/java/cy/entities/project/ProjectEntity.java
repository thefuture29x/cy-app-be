package cy.entities.project;

import cy.entities.UserEntity;
import cy.entities.project.Listener.ProjectListener;
import cy.utils.Const;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;
import java.util.Set;
@HistoryLogTitle(title = "project")
@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_projects")
@HistoryLogTitle(title = "project")
public class ProjectEntity extends ProjectBaseEntity{

    @HistoryLogTitle(title = "", ignore = true )
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
