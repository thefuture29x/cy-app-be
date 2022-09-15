package cy.models.attendance;

import cy.entities.attendance.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationModel {
    private Long id;
    private String title;
    private String content;

    private Long requestAttendId;
    private Long requestDayOffId;
    private Long requestDeviceId;
    private Long requestModifiId;
    private Long requestOTId;

    public static NotificationEntity toEntity(NotificationModel model){
        if(model == null) return null;
        return NotificationEntity.builder()
                .id(model.getId())
                .title(model.getTitle())
                .content(model.getContent())
                .build();
    }
}
