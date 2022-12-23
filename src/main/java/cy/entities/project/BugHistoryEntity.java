package cy.entities.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@HistoryLogTitle(title = "lịch sử bug")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tbl_bug_historys")
public class BugHistoryEntity {
    @HistoryLogTitle(title = "", ignore = true)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @HistoryLogTitle(title = "", ignore = true)
//    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id")
    private Long bugId;

    @HistoryLogTitle(title = "ngày bắt đầu")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @HistoryLogTitle(title = "ngày kết thúc")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='BUG_HISTORY'")
    private List<FileEntity> attachFiles;

}
