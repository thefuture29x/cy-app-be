package cy.entities.project;

import cy.entities.common.HistoryLogTitle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@HistoryLogTitle(title = "lịch sử pending")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "tbl_pending_historys")
public class PendingHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @HistoryLogTitle(title = "thời gian bắt đầu")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startDate;
    @HistoryLogTitle(title = "thời gian kết thúc")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @Column(name = "object_id")
    private Long objectId;
    private String category;
    private String statusBeforePending;
    private String statusAfterPending;
}