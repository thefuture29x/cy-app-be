package cy.services.attendance;

import cy.dtos.attendance.NotificationDto;
import cy.entities.attendance.NotificationEntity;
import cy.models.attendance.NotificationModel;
import cy.services.common.IBaseService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface INotificationService extends IBaseService<NotificationEntity, NotificationDto, NotificationModel, Long> {
    Page<NotificationDto> findAllByUserId(Pageable pageable);

    Page<NotificationDto> findAllByUserIdNotRead(Pageable pageable);
}
