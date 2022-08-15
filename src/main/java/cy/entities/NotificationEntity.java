package cy.entities;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_notification")
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "content")
    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userId;

    @ManyToOne
    @JoinColumn(name = "request_attend_id")
    private RequestAttendEntity requestAttendEntityId;

    @ManyToOne
    @JoinColumn(name = "request_dayoff_id")
    private RequestDayOffEntity requestDayOff;

    @ManyToOne
    @JoinColumn(name = "request_device_id")
    private RequestDeviceEntity requestDevice;

    @ManyToOne
    @JoinColumn(name = "request_modifi_id")
    private RequestModifiEntity requestModifi;

    @ManyToOne
    @JoinColumn(name = "request_OT_id")
    private RequestOTEntity requestOT;
}
