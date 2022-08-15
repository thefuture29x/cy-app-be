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
@Table(name = "tbl_request_device")
public class RequestDeviceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "type")
    private String type;
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

    @OneToMany(mappedBy = "requestDevice")
    private List<HistoryRequestEntity> historyRequestEntities;


}
