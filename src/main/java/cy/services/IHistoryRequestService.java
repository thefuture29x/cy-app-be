package cy.services;

import cy.dtos.HistoryRequestDto;
import cy.entities.HistoryRequestEntity;
import cy.models.HistoryRequestModel;

public interface IHistoryRequestService extends IBaseService<HistoryRequestEntity,HistoryRequestDto,HistoryRequestModel,Long>{
    HistoryRequestDto saveOrUpdate(HistoryRequestModel historyRequestModel);
    HistoryRequestDto findById(Long id);

}
