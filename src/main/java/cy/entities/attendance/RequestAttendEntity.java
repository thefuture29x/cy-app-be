package cy.entities.attendance;

import cy.entities.common.UserEntity;
import lombok.*;

import javax.persistence.*;

import java.sql.Date;
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
    //  status:
    // 0: waiting for approve
    // 1: approved
    // 2: rejected
    @Column(name = "status")
    private Integer status;
    @Column(name = "reason_cancel")
    private String reasonCancel;
    @Column(name = "files")
    private String files;

    //@CreationTimestamp
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date createdDate;
    //@UpdateTimestamp
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date updatedDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createBy;

    @ManyToOne
    @JoinColumn(name = "assign_id")
    private UserEntity assignTo;
//    @CreationTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "created_date")
//    private Date createdDate;
//    @UpdateTimestamp
//    @Temporal(TemporalType.TIMESTAMP)
//    @Column(name = "updated_date")
//    private Date updatedDate;
    @OneToMany(mappedBy = "requestAttend")
    private List<HistoryRequestEntity> historyRequestEntities;

    @PrePersist
    public void prePersist(){
        this.createdDate = new java.util.Date();
        this.updatedDate = new java.util.Date();
    }
    @PreUpdate
    public void preUpdate(){
        this.updatedDate = new java.util.Date();
    }
}
