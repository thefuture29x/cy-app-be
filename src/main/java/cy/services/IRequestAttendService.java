package cy.services;

import cy.dtos.RequestAttendDto;
import cy.entities.RequestAttendEntity;
import cy.models.RequestAttendModel;

import java.sql.Date;

public interface IRequestAttendService extends IBaseService<RequestAttendEntity, RequestAttendDto,
        RequestAttendModel, Long> {
    RequestAttendDto changeRequestStatus(Long id, String reasonCancel, boolean status);

    Boolean checkRequestAttendNotExist(String dayRequestAttend);

    Boolean checkRequestAttendExist(Date dayRequestAttend);

    Long totalDayOfAttendInMonth(Long userId, java.util.Date beginDate, java.util.Date endDate);
}
