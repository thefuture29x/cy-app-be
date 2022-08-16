package cy.models;

import cy.entities.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
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