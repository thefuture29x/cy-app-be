package cy.entities.project;

import cy.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Parent;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuperBuilder(toBuilder = true)
@Table(name = "tbl_tasks")
public class TaskEntity extends ProjectBaseEntity{
    private String priority;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="feature_id")
    private FeatureEntity feature;

    @OneToMany
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    @Where(clause = "category='tbl_tasks'")
    private List<FileEntity> attachFiles;
}
