package cy.services;

import cy.dtos.RequestOTDto;
import cy.entities.RequestOTEntity;
import cy.models.RequestOTModel;

public interface IRequestOTService extends IBaseService<RequestOTEntity, RequestOTDto, RequestOTModel, Long>{
    RequestOTDto responseOtRequest(Long requestOtId, String reasonCancel, Boolean status);
    Float totalOTHours(Long userId, Integer status, Integer typeOt, String startDate, String endDate);
}
