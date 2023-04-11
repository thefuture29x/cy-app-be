package cy.entities.attendance;

import cy.entities.common.UserEntity;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_request_device")
public class RequestDeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type")
    private String type;//Loại thiết bị mượn
    @Column(name = "type_request_device")
    private Integer typeRequestDevice;//mượn hay là mua?
    //0 là mượn
    //1 là mua
    //2 là đã trả
    @Column(name = "title")
    private String title;
    @Column(name = "quantity")
    private Integer quantity;


    @Column(name = "date_request_device")
    private Date dateRequestDevice;
    @Column(name = "date_start")
    private Date dateStart;
    @Column(name = "date_end")
    private Date dateEnd;

    @Column(name = "status")
    private Integer status;
    // 3 status:
    // 0: waiting for approve
    // 1: approved
    // 2: rejected
    //Đã phê duyệt hay chưa
    @Column(name = "reason_cancel")
    private String reasonCancel;
    @Column(name = "files")
    private String files;
    @Column(name = "description")
    private String description;


    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity createBy;

    @ManyToOne
    @JoinColumn(name = "assign_id")
    private UserEntity assignTo;

    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @UpdateTimestamp
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @OneToMany(mappedBy = "requestDevice")
    private List<HistoryRequestEntity> historyRequestEntities;
}
