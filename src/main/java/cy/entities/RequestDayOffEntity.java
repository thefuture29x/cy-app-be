package cy.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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
@Table(name = "tbl_request_dayoff")
public class RequestDayOffEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "date_request_dayoff")
    @JsonSerialize(as = Date.class)
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd")
    private Date dateDayOff;
    @Column(name = "status")
    private Integer status;
    @Column(name = "type_off")
    private Integer typeOff;
    //0 Là nghỉ buổi sáng
    //1 Là nghỉ buổi chiều
    //2 Là nghỉ buổi cả ngày
    @Column(name = "is_legit")
    private Boolean isLegit;
    //true là có lương
    //false là ko lương
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

    @CreationTimestamp
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @UpdateTimestamp
    @Column(name = "updated_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @OneToMany(mappedBy = "requestDayOff")
    private List<HistoryRequestEntity> historyRequestEntities;

}
