package cy.entities.mission;

import cy.entities.common.FileEntity;
import cy.entities.common.HistoryLogTitle;
import cy.entities.common.UserEntity;
import cy.entities.project.Listener.ProjectListener;
import cy.entities.project.ProjectBaseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@HistoryLogTitle(title = "assignCheckList")
@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_assign_check_list")
public class AssignCheckListEntity {
    @HistoryLogTitle(title = "", ignore = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @HistoryLogTitle(title = "nội dung")
    private String content;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="assign_id")
    private AssignEntity assign;

    @HistoryLogTitle(title = "hoàn thành", ignore = true)
    private Boolean isDone = false;

    @HistoryLogTitle(title = "", ignore = true)
    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @HistoryLogTitle(title = "", ignore = true)
    @UpdateTimestamp
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
}