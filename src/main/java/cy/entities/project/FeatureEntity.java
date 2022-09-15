package cy.entities.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_features")
public class FeatureEntity extends ProjectBaseEntity{
    private String priority;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="project_id")
    private ProjectEntity project;

    @OneToMany
    @JoinColumn(name = "object_id", insertable = false, updatable = false)
    @Where(clause = "category='FEATURE'")
    private List<FileEntity> attachFiles;
}