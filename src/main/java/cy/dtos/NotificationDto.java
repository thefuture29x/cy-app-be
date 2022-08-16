package cy.dtos;

import cy.entities.NotificationEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class NotificationDto {
    private Long id;
    private String title;
    private String content;

    private Long userId;
    private Long requestAttendId;
    private Long requestDayOffId;
    private Long requestDeviceId;
    private Long requestModifiId;
    private Long requestOTId;

    public static NotificationDto toDto(NotificationEntity entity){
        if(entity == null) return null;
        return NotificationDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .content(entity.getContent())
                .userId(entity.getUserId().getUserId())
                .requestAttendId(entity.getRequestAttendEntityId() == null ? null : entity.getRequestAttendEntityId().getId())
                .requestDayOffId(entity.getRequestDayOff() == null ? null : entity.getRequestDayOff().getId())
                .requestDeviceId(entity.getRequestDevice() == null ? null : entity.getRequestDevice().getId())
                .requestModifiId(entity.getRequestModifi() == null ? null : entity.getRequestModifi().getId())
                .requestOTId(entity.getRequestOT() == null ? null : entity.getRequestOT().getId())
                .build();
    }
}
