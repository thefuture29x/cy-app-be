package cy.entities.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.sql.Time;
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
    @JoinColumn(name = "bug_id")
    private Long bugId;

    @HistoryLogTitle(title = "ngày bắt đầu")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @HistoryLogTitle(title = "ngày kết thúc")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    private Boolean isPending = false;

    @HistoryLogTitle(title = "file đính kèm", isMultipleFiles = true)
    @OneToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JoinColumn(name = "object_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    @Where(clause = "category='BUG_HISTORY'")
    private List<FileEntity> attachFiles;

    @HistoryLogTitle(title = "chi tiết")
    private String detail;

    @HistoryLogTitle(title = "ngày bắt đầu ước tính")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDateEstimate;
    @HistoryLogTitle(title = "ngày kết thúc ước tính")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDateEstimate;

    private int totalMinuteEstimate;
    private int totalMinuteExecution;

    @Temporal(TemporalType.TIMESTAMP)
    private Date deadLine;
    private String timeEstimate;
    private String timeExecution;

}
