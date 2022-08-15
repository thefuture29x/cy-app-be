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
@Table(name = "tbl_request_attend")
public class RequestAttendEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "time_check_in")
    private String timeCheckIn;
    @Column(name = "time_check_out")
    private String timeCheckOut;
    @Column(name = "date_request_attend")
    private Date dateRequestAttend;
    @Column(name = "status")
    private Integer status;
    @Column(name = "reason_cancel")
    private String reasonCancel;
    @Column(name = "files")
    private String files;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createBy;

    @ManyToOne
    @JoinColumn(name = "assign_id")
    private UserEntity assignTo;

    @OneToMany(mappedBy = "requestAttend")
    private List<HistoryRequestEntity> historyRequestEntities;


}
