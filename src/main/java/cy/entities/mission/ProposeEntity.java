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

@HistoryLogTitle(title = "propose")
@EntityListeners(ProjectListener.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder(toBuilder = true)
@Entity
@Table(name = "tbl_propose")
public class ProposeEntity {
    @HistoryLogTitle(title = "", ignore = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @HistoryLogTitle(title = "nội dung")
    @Column(columnDefinition="TEXT")
    private String description;

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='PROPOSE'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createBy;

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

    @HistoryLogTitle(title = "", ignore = true)
    private String category;

    @HistoryLogTitle(title = "", ignore = true)
    private Long objectId;
}