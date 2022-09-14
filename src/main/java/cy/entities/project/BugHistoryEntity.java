package cy.entities.project;

import cy.entities.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;
import org.hibernate.annotations.WhereJoinTable;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_bug_historys")
public class BugHistoryEntity extends ProjectBaseEntity{

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id")
    private BugEntity bugId;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @ManyToMany
    @WhereJoinTable(clause = "category='tbl_bug_historys'")
    @JoinTable(name = "tbl_tag_relations",
            joinColumns = @JoinColumn(name = "object_id", insertable = false, updatable = false),
            inverseJoinColumns = {@JoinColumn(name = "tag_id", insertable = false, updatable = false)})
    Set<TagEntity> tagEntities;

}
