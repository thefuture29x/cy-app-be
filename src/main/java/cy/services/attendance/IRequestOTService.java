package cy.services.attendance;

import cy.dtos.attendance.RequestOTDto;
import cy.entities.attendance.RequestOTEntity;
import cy.models.attendance.RequestOTModel;
import cy.services.common.IBaseService;

public interface IRequestOTService extends IBaseService<RequestOTEntity, RequestOTDto, RequestOTModel, Long> {
    RequestOTDto responseOtRequest(Long requestOtId, String reasonCancel, Boolean status);
    Float totalOTHours(Long userId, Integer status, Integer typeOt, String startDate, String endDate);
}
