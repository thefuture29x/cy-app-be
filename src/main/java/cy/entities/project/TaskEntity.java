package cy.entities.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
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
