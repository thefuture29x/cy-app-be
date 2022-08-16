package cy.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_request_OT")
public class RequestOTEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "time_start")
    private String timeStart;
    @Column(name = "time_End")
    private String timeEnd;
    @Temporal(TemporalType.DATE)
    @Column(name = "date_OT")
    private Date dateOT;
    @Column(name = "status")
    private Integer status;
    @Column(name = "reason_cancel")
    private String reasonCancel;
    @Column(name = "description")
    private String description;
    @Column(name = "files")
    private String files;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createBy;

    @ManyToOne
    @JoinColumn(name = "assign_id")
    private UserEntity assignTo;

    @OneToMany(mappedBy = "requestOT")
    private List<HistoryRequestEntity> historyRequestEntities;


}
