package cy.services;

import cy.dtos.NotificationDto;
import cy.entities.NotificationEntity;
import cy.models.NotificationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface INotificationService extends IBaseService<NotificationEntity, NotificationDto, NotificationModel, Long>{
    Page<NotificationDto> findAllByUserId(Pageable pageable);

    Page<NotificationDto> findAllByUserIdNotRead(Pageable pageable);
}
