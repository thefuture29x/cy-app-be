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
public class ProjectEntity extends ProjectBaseEntity{

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='PROJECT'")
    private List<FileEntity> attachFiles;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "avatar_id")
    private FileEntity avatar;


    @Transient
    private List<UserEntity> devTeam;
    @Transient
    private List<UserEntity> followTeam;
    @Transient
    private List<UserEntity> viewTeam;


}
