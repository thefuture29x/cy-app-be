package cy.entities;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tbl_history_request")
public class HistoryRequestEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "date_history")
    private Date dateHistory;
    @Column(name = "time_history")
    private String timeHistory;
    @Column(name = "status")
    private String status;
    @ManyToOne
    @JoinColumn(name = "request_attend_id")
    private RequestAttendEntity requestAttend;

    @ManyToOne
    @JoinColumn(name = "request_dayoff_id")
    private RequestDayOffEntity requestDayOff;

    @ManyToOne
    @JoinColumn(name = "request_OT_id")
    private RequestOTEntity requestOT;

    @ManyToOne
    @JoinColumn(name = "request_modifi_id")
    private RequestModifiEntity requestModifi;

    @ManyToOne
    @JoinColumn(name = "request_device_id")
    private RequestDeviceEntity requestDevice;
}
