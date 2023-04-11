package cy.services.attendance;

import cy.dtos.attendance.RequestAttendDto;
import cy.entities.attendance.RequestAttendEntity;
import cy.models.attendance.RequestAttendModel;
import cy.services.common.IBaseService;

import java.sql.Date;

public interface IRequestAttendService extends IBaseService<RequestAttendEntity, RequestAttendDto,
        RequestAttendModel, Long> {
    RequestAttendDto changeRequestStatus(Long id, String reasonCancel, boolean status);

    Boolean checkRequestAttendNotExist(String dayRequestAttend);

    Boolean checkRequestAttendExist(Date dayRequestAttend);

    Long totalDayOfAttendInMonth(Long userId, java.util.Date beginDate, java.util.Date endDate);
}
