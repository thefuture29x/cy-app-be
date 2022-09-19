package cy.entities.project;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@HistoryLogTitle(title = "lịch sử bug")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tbl_bug_historys")
public class BugHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @HistoryLogTitle(title = "", ignore = true)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bug_id")
    private BugEntity bugId;

    @HistoryLogTitle(title = "ngày bắt đầu")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @HistoryLogTitle(title = "ngày kết thúc")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;


}
